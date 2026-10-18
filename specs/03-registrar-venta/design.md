# Spec 03 · Registrar venta — Diseño

El diagrama de secuencia completo de este flujo está en `specs/arquitectura/arquitectura.md`, sección 7. Aquí está el detalle de cada componente.

## Responsabilidad de cada capa en este caso de uso

| Capa | Hace | No hace |
|---|---|---|
| **Presentación** (`CheckoutController`) | Valida **formato** del formulario (Bean Validation), arma `SolicitudVentaDTO` con los ítems de la sesión, vacía la sesión si todo salió bien, redirige | Calcular totales, revisar stock, abrir transacciones |
| **Aplicación** (`VentaService`) | Abre la **transacción**, carga productos, pide al dominio que construya la venta, ordena descontar stock y guardar, convierte a `ComprobanteDTO` | Reglas de negocio, SQL |
| **Dominio** (`Carrito`, `Cliente`, `Venta`, `LineaVenta`, `Totales`) | Valida RN-02 a RN-08 y RN-10, calcula totales, congela nombre y precio en las líneas | Saber de HTTP, transacciones o SQL |
| **Persistencia** (`JdbcProductoDAO`, `JdbcVentaDAO`) | `UPDATE` condicional de stock, `INSERT` de venta y líneas, generación de id | Decidir si una venta es válida |

## Dominio

```java
/** Dominio · datos del comprador (RN-08). */
public record Cliente(String nombre, String email, String dni) {
    // Constructor compacto: trim; valida longitud del nombre (3–80), email con regex simple
    // (^[^@\s]+@[^@\s]+\.[^@\s]+$) y DNI ^\d{8}$. Si falla → DatosClienteInvalidosException
    // con el primer problema encontrado.
}

/** Dominio · línea de una venta registrada; congela nombre y precio (RN-10). */
public record LineaVenta(Long productoId, String nombreProducto, BigDecimal precioUnitario,
                         int cantidad, BigDecimal importe) {
    public static LineaVenta desde(LineaCarrito linea)
}

/** Dominio · venta confirmada. Inmutable. */
public class Venta {
    private final Long id;                 // null hasta que la persistencia la guarde
    private final LocalDateTime fecha;
    private final Cliente cliente;
    private final List<LineaVenta> lineas;
    private final Totales totales;

    /** Crea una venta nueva desde un carrito válido. RN-07: carrito vacío → CarritoVacioException. */
    public static Venta crear(Cliente cliente, Carrito carrito, LocalDateTime fecha)

    /** Para la persistencia: rearma una venta ya guardada con sus totales históricos (no se recalculan). */
    public static Venta reconstruir(Long id, LocalDateTime fecha, Cliente cliente,
                                    List<LineaVenta> lineas, Totales totales)

    public Venta conId(Long id)            // devuelve una copia con id
    public String numero()                 // "V-%06d"; IllegalStateException si id == null  (RN-10)
    public int totalUnidades()
    // getters
}
```

### Excepciones nuevas (`dominio.excepcion`)

| Excepción | Código | Mensaje |
|---|---|---|
| `CarritoVacioException` | `CARRITO_VACIO` | "El carrito está vacío" |
| `DatosClienteInvalidosException` | `DATOS_CLIENTE_INVALIDOS` | El problema concreto (p. ej. "El DNI debe tener 8 dígitos") |
| `VentaNoEncontradaException` | `VENTA_NO_ENCONTRADA` | "La venta solicitada no existe" |

## Aplicación

```java
public record SolicitudVentaDTO(String nombre, String email, String dni, List<ItemCarritoDTO> items) {}

public record LineaVentaDTO(String nombreProducto, BigDecimal precioUnitario, int cantidad, BigDecimal importe) {}

public record ComprobanteDTO(Long id, String numero, LocalDateTime fecha,
                             String clienteNombre, String clienteEmail, String clienteDni,
                             List<LineaVentaDTO> lineas, int totalUnidades,
                             BigDecimal subtotal, BigDecimal descuento, BigDecimal baseImponible,
                             BigDecimal igv, BigDecimal total) {
    public static ComprobanteDTO desde(Venta venta)
}
```

```java
/** Aplicación · caso de uso "Registrar venta". */
@Service
public class VentaService {
    VentaService(ProductoDAO productoDAO, VentaDAO ventaDAO)

    @Transactional
    public ComprobanteDTO registrarVenta(SolicitudVentaDTO solicitud) {
        // 1. si solicitud.items() está vacío → CarritoVacioException                      (RN-07)
        // 2. productos = productoDAO.buscarPorIds(ids)  — un id ausente → ProductoNoEncontradoException
        // 3. carrito = new Carrito(); por cada ítem: carrito.agregar(producto, cantidad)   (RN-02, RN-03, RN-04)
        //    → aquí se revalida el stock con datos actuales, ANTES de tocar nada
        // 4. cliente = new Cliente(nombre, email, dni)                                     (RN-08)
        // 5. venta = Venta.crear(cliente, carrito, LocalDateTime.now())                    (RN-05, RN-06, RN-10)
        // 6. por cada línea: if (!productoDAO.descontarStock(id, cantidad))
        //        throw new StockInsuficienteException(...)   → rollback de los descuentos previos  (RN-09)
        // 7. guardada = ventaDAO.guardar(venta)
        // 8. return ComprobanteDTO.desde(guardada)
    }
}

/** Aplicación · consultas de ventas registradas (la spec 04 agrega listar). */
@Service
public class ConsultaVentasService {
    ConsultaVentasService(VentaDAO ventaDAO)
    public ComprobanteDTO obtener(Long id)    // VentaNoEncontradaException si no existe
}
```

**Por qué el paso 3 y el paso 6 validan dos veces el stock**: el paso 3 (dominio) da el mensaje correcto en el caso normal; el paso 6 (SQL condicional) protege contra la **condición de carrera** entre el paso 3 y el `UPDATE` cuando dos compradores compran a la vez (EC04). Si el paso 6 falla, la excepción de tiempo de ejecución hace que Spring revierta la transacción completa.

## Persistencia

### `JdbcProductoDAO.descontarStock`

```sql
UPDATE producto SET stock = stock - :cantidad WHERE id = :id AND stock >= :cantidad
```

Devuelve `filasAfectadas == 1`. Nunca lee primero y escribe después.

### `JdbcVentaDAO`

| Método | SQL |
|---|---|
| `guardar(venta)` | `INSERT INTO venta (fecha, cliente_nombre, cliente_email, cliente_dni, subtotal, descuento, base_imponible, igv, total) VALUES (…)` con `KeyHolder` para el id; luego `INSERT INTO linea_venta (…)` por cada línea; devuelve `venta.conId(id)` |
| `buscarPorId(id)` | `SELECT … FROM venta WHERE id = :id` + `SELECT … FROM linea_venta WHERE venta_id = :id ORDER BY id` → `Venta.reconstruir(...)` |
| `listar()` | `SELECT … FROM venta ORDER BY id DESC` + `SELECT … FROM linea_venta ORDER BY venta_id, id` agrupado en Java por `venta_id` |

### `MemoriaProductoDAO.descontarStock` y `MemoriaVentaDAO`

- `descontarStock` es `synchronized`: compara y descuenta de forma atómica.
- `MemoriaVentaDAO` usa `AtomicLong` para el id y un `ConcurrentHashMap<Long, Venta>`.
- **Limitación conocida** (se menciona si preguntan): las colecciones en memoria no participan de la transacción. Como el paso 3 ya validó todo el stock, un fallo en el paso 6 solo puede ocurrir con compras simultáneas. La garantía de EC04 se demuestra con el perfil por defecto (JDBC + H2).

## Presentación

```java
/** Presentación · datos del formulario de checkout, con validación de formato. */
public class DatosClienteForm {
    @NotBlank @Size(min = 3, max = 80, message = "El nombre debe tener entre 3 y 80 caracteres") String nombre;
    @NotBlank @Email(message = "Ingresa un email válido") String email;
    @NotBlank @Pattern(regexp = "\\d{8}", message = "El DNI debe tener 8 dígitos") String dni;
}
```

| Ruta | Comportamiento |
|---|---|
| `GET /checkout` | `resumen = carritoService.resumir(sesion.items())`; si `!resumen.listoParaComprar()` → `redirect:/carrito` + flash error; si no → vista `checkout/formulario` con `resumen` y `DatosClienteForm` vacío |
| `POST /checkout` | `@Valid DatosClienteForm` + `BindingResult`. Con errores → vuelve a `checkout/formulario` (recalcula `resumen`). Sin errores → `ventaService.registrarVenta(...)`, `sesion.vaciar()`, `redirect:/ventas/{id}` + flash `exito` |
| `GET /ventas/{id}` | `consultaVentasService.obtener(id)` → vista `checkout/comprobante` |

Si `registrarVenta` lanza `ReglaNegocioException` (p. ej. stock insuficiente por una compra simultánea), `ManejadorErroresWeb` redirige a `/checkout` con el mensaje; el `GET /checkout` detecta la línea sin stock y manda a `/carrito`, donde se ve marcada (CAR-5.1). La sesión **no** se vacía.

### Plantillas

- `checkout/formulario.html`: tabla resumen (solo lectura) + formulario con errores por campo (`th:errors`). Botón "Confirmar compra" que se deshabilita al enviar (evita doble clic).
- `checkout/comprobante.html`: número grande `V-000001`, fecha `dd/MM/yyyy HH:mm`, cliente, tabla de líneas, totales, botón "Volver al catálogo".

## Pruebas

| Prueba | Tipo | Criterios |
|---|---|---|
| `ClienteTest` | Dominio puro | RN-08: límites 2/3/80/81 caracteres, emails válidos e inválidos, DNI con 7, 8, 9 dígitos y con letras |
| `VentaTest` | Dominio puro | `crear` con carrito vacío (RN-07); líneas congeladas (RN-10); totales iguales a `carrito.totales()`; `numero()` con id 1 → `V-000001` |
| `VentaServiceTest` | Aplicación con DAO en memoria, sin Spring | Venta feliz (stock descontado, venta guardada), stock insuficiente (nada cambia), carrito vacío, producto inexistente, datos de cliente inválidos |
| `JdbcVentaDAOTest` | `@JdbcTest` | `guardar` + `buscarPorId` devuelven la misma venta con sus líneas y totales; `listar` en orden descendente |
| `JdbcProductoDAODescuentoTest` | `@JdbcTest` | `descontarStock` devuelve false sin modificar cuando no alcanza; el stock nunca queda negativo |
| `RegistrarVentaIntegracionTest` | `@SpringBootTest` (perfil por defecto) | VEN-3.4: con dos líneas donde la segunda no alcanza, **el stock de la primera tampoco cambia** (rollback real) |
| `CompraSimultaneaTest` | `@SpringBootTest`, dos hilos con `CountDownLatch` | VEN-3.5 / EC04: producto 6 (stock 1), dos compras simultáneas → 1 éxito, 1 `StockInsuficienteException`, stock final 0, 1 venta registrada |
| `CheckoutControllerTest` | `@WebMvcTest` | Errores de formato no llaman al servicio (VEN-2.4); éxito vacía sesión y redirige (VEN-4.1); carrito vacío redirige a `/carrito` (VEN-1.2) |
