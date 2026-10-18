# Spec 01 · Registrar venta — Diseño

Diseño por capa. Carpetas: `frontend/`, `backend/` (paquete base `pe.edu.ulima.tiendacapas`) y `base-de-datos/` (ver `estructura.md`).

## Base de datos (`base-de-datos/`)

`base-de-datos/schema.sql`

```sql
CREATE TABLE producto (
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre  VARCHAR(80)   NOT NULL,
    precio  DECIMAL(10,2) NOT NULL,
    stock   INT           NOT NULL CHECK (stock >= 0)
);

CREATE TABLE venta (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    producto_id BIGINT        NOT NULL REFERENCES producto(id),
    cliente     VARCHAR(80)   NOT NULL,
    cantidad    INT           NOT NULL,
    total       DECIMAL(10,2) NOT NULL,
    fecha       TIMESTAMP     NOT NULL
);
```

`base-de-datos/data.sql`

```sql
INSERT INTO producto (nombre, precio, stock) VALUES ('Mouse', 25.00, 10);
INSERT INTO producto (nombre, precio, stock) VALUES ('Teclado', 80.00, 5);
INSERT INTO producto (nombre, precio, stock) VALUES ('Monitor 24"', 450.00, 2);
```

`base-de-datos/consultas.sql` (solo para la demo en `/h2-console`)

```sql
SELECT * FROM producto ORDER BY id;
SELECT * FROM venta ORDER BY id DESC;
SELECT v.id, p.nombre, v.cliente, v.cantidad, v.total, v.fecha
FROM venta v JOIN producto p ON p.id = v.producto_id
ORDER BY v.id DESC;
```

## Modelo (compartido)

```java
public record Producto(Long id, String nombre, BigDecimal precio, int stock) {}

public record Venta(Long id, Long productoId, String producto, String cliente,
                    int cantidad, BigDecimal total, LocalDateTime fecha) {}
```

`Venta.producto` es el nombre del producto (lo llena el `JOIN` al listar), para que la tabla del frontend no tenga que buscarlo.

## Capa de Datos (`datos`)

`ProductoRepository` (`@Repository`, recibe `JdbcTemplate` por constructor)

| Método | SQL |
|---|---|
| `List<Producto> listar()` | `SELECT id, nombre, precio, stock FROM producto ORDER BY id` |
| `Optional<Producto> buscarPorId(Long id)` | `SELECT id, nombre, precio, stock FROM producto WHERE id = ?` |
| `boolean descontarStock(Long id, int cantidad)` | `UPDATE producto SET stock = stock - ? WHERE id = ? AND stock >= ?`; devuelve `true` si actualizó 1 fila y `false` si no había stock suficiente |

El `AND stock >= ?` hace que la comprobación y el descuento sean **una sola operación** en la BD: si dos ventas llegan a la vez, solo descuenta la que alcanza. El `CHECK (stock >= 0)` de la tabla es una segunda barrera. El repositorio **no decide** qué hacer si no alcanza; solo lo informa con el `boolean`.

`VentaRepository` (`@Repository`)

| Método | SQL |
|---|---|
| `Venta guardar(Venta v)` | `INSERT INTO venta (producto_id, cliente, cantidad, total, fecha) VALUES (?, ?, ?, ?, ?)`; devuelve la venta con el `id` generado (`KeyHolder`). |
| `List<Venta> listar()` | `SELECT v.id, v.producto_id, p.nombre, v.cliente, v.cantidad, v.total, v.fecha FROM venta v JOIN producto p ON p.id = v.producto_id ORDER BY v.id DESC` |

Cada método escribe `log.info("[DATOS] ...")` con la sentencia que ejecuta.

## Capa de Negocio (`negocio`)

`VentaException extends RuntimeException` — error de regla de negocio; su mensaje se muestra tal cual al usuario.

`VentaService` (`@Service`, recibe `ProductoRepository` y `VentaRepository` por constructor)

| Método | Qué hace |
|---|---|
| `List<Producto> listarProductos()` | Delega en `ProductoRepository.listar()` |
| `List<Venta> listarVentas()` | Delega en `VentaRepository.listar()` |
| `@Transactional Venta registrarVenta(Long productoId, String cliente, Integer cantidad)` | Ver pasos |

Pasos de `registrarVenta`:

1. `log.info("[NEGOCIO] registrarVenta ...")`.
2. RN-01: si `cantidad == null || cantidad < 1` → `VentaException("La cantidad debe ser al menos 1")`; si `cliente` es nulo o en blanco → `VentaException("Ingresa el nombre del cliente")`; si `cliente.trim().length() > 80` → `VentaException("El nombre del cliente no puede superar 80 caracteres")`.
3. `buscarPorId(productoId)`; si no existe → `VentaException("El producto no existe")`.
4. RN-02: si `cantidad > producto.stock()` → `VentaException("Stock insuficiente: disponible " + stock)`.
5. RN-03: `total = producto.precio().multiply(BigDecimal.valueOf(cantidad)).setScale(2, RoundingMode.HALF_UP)`.
6. Si `descontarStock(productoId, cantidad)` devuelve `false` (otra venta se llevó el stock entre el paso 4 y este), volver a leer el producto y lanzar `VentaException("Stock insuficiente: disponible " + stockActual)` (RN-02, VEN-3.7).
7. `guardar(new Venta(null, productoId, producto.nombre(), cliente.trim(), cantidad, total, LocalDateTime.now()))` y devolver el resultado.

## Capa de Presentación

### Backend: `presentacion`

`VentaRequest` — `record VentaRequest(Long productoId, String cliente, Integer cantidad)`.

`VentaController` (`@RestController @RequestMapping("/api")`, recibe solo `VentaService`)

| Endpoint | Llama a | Respuesta |
|---|---|---|
| `GET /api/productos` | `listarProductos()` | `200` + lista JSON |
| `GET /api/ventas` | `listarVentas()` | `200` + lista JSON |
| `POST /api/ventas` (body `VentaRequest`) | `registrarVenta(...)` | `201` + venta JSON |
| `@ExceptionHandler(VentaException)` | — | `400` + `{"error": "mensaje"}` |

Cada endpoint escribe `log.info("[PRESENTACIÓN] ...")`. El controlador **no valida reglas**: todo pasa al servicio.

### Frontend: `frontend/`

`index.html`: título "TiendaCapas · Registrar venta", formulario (select `producto`, input numérico `cantidad`, input texto `cliente` con `maxlength="80"`, botón "Registrar venta"), un `<p id="mensaje">` y una tabla de ventas.

`app.js`:

- `cargarProductos()`: `fetch('/api/productos')` → llena el select.
- `cargarVentas()`: `fetch('/api/ventas')` → llena la tabla.
- Al enviar el formulario: `fetch('/api/ventas', {method: 'POST', headers: {'Content-Type': 'application/json'}, body: JSON.stringify({...})})`.
  - Si `res.ok`: mensaje verde "Venta registrada: S/ total", limpiar el formulario y volver a llamar `cargarProductos()` y `cargarVentas()`.
  - Si no: mostrar `error` del JSON en rojo.
- Al cargar la página: `cargarProductos()` y `cargarVentas()`.

`estilos.css`: unas pocas reglas para que se lea bien en la grabación (fuente grande, tabla con bordes, verde/rojo para mensajes).

## Pruebas

`VentaServiceTest` (`@SpringBootTest`, usa la BD H2 con los datos semilla; `@Transactional` en la clase para que cada prueba revierta sus cambios)

| Prueba | Verifica |
|---|---|
| `registraVentaYDescuentaStock` | Vender 3 "Mouse" → total 75.00 y stock pasa de 10 a 7 (VEN-2, RN-02, RN-03) |
| `rechazaStockInsuficiente` | Vender 3 "Monitor 24\"" (stock 2) → `VentaException` "Stock insuficiente: disponible 2" y stock sigue en 2 (VEN-3.3, VEN-3.5) |
| `rechazaCantidadCero` | Cantidad 0 → `VentaException` "La cantidad debe ser al menos 1" (VEN-3.1) |
| `rechazaClienteMuyLargo` | Cliente de 81 caracteres → `VentaException` "El nombre del cliente no puede superar 80 caracteres" (VEN-3.6) |
