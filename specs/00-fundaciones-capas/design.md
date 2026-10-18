# Spec 00 · Fundaciones de las capas — Diseño

## Componentes que crea esta spec

| Clase / archivo | Capa | Responsabilidad |
|---|---|---|
| `TiendaCapasApplication` | raíz | `@SpringBootApplication`. No referencia ninguna clase de las capas. |
| `transversal.error.ReglaNegocioException` | Transversal | Excepción base, Java puro (`RuntimeException`) |
| `transversal.traza.TrazaCapasAspect` | Transversal | Aspecto `@Around` que imprime la traza por capa |
| `dominio.repositorio.ProductoDAO`, `VentaDAO` | Dominio | Contratos de acceso a datos (completos aquí; los usan las specs 01–04) |
| `dominio.modelo.Producto` | Dominio | Entidad mínima para la semilla (sus reglas se completan en la spec 01) |
| `persistencia.jdbc.JdbcProductoDAO`, `JdbcVentaDAO` | Persistencia | Esqueleto con `@Repository @Profile("!memoria")` |
| `persistencia.memoria.MemoriaProductoDAO`, `MemoriaVentaDAO`, `DatosSemillaMemoria` | Persistencia | Esqueleto con `@Repository @Profile("memoria")` |
| `presentacion.error.ManejadorErroresWeb` | Presentación | `@ControllerAdvice` |
| `presentacion.web.InicioController` | Presentación | `GET /` → `redirect:/productos` |
| `schema.sql`, `data.sql` | Recursos | Esquema y semilla de H2 |
| `templates/fragmentos/layout.html`, `error.html`, `static/css/estilos.css` | Recursos | Plantilla base (cabecera con enlaces Catálogo · Carrito (n) · Ventas) |
| `test/.../arquitectura/ArquitecturaCapasTest` | Prueba | Reglas AR-01 a AR-07 |

## Contratos DAO (interfaces en `dominio.repositorio`)

```java
/** Dominio · contrato de acceso a productos. La implementan las clases de persistencia. */
public interface ProductoDAO {
    List<Producto> listar(String texto, String categoria);   // ambos opcionales (null o vacío = sin filtro); orden por nombre
    Optional<Producto> buscarPorId(Long id);
    List<Producto> buscarPorIds(Collection<Long> ids);
    List<String> listarCategorias();                          // distintas, orden alfabético
    boolean descontarStock(Long id, int cantidad);            // true si descontó; false si no había stock suficiente
}

/** Dominio · contrato de acceso a ventas. */
public interface VentaDAO {
    Venta guardar(Venta venta);                // inserta venta + líneas; devuelve la venta con id asignado
    Optional<Venta> buscarPorId(Long id);      // con sus líneas
    List<Venta> listar();                      // más reciente primero, cada una con sus líneas
}
```

`Venta` se termina de definir en la spec 03; en esta spec basta con que la interfaz compile (puede crearse la clase `Venta` vacía como marcador y completarse luego).

## Excepción base

```java
/** Transversal · base de toda regla de negocio violada. Java puro: la pueden usar todas las capas. */
public abstract class ReglaNegocioException extends RuntimeException {
    private final String codigo;   // p. ej. "STOCK_INSUFICIENTE"
    protected ReglaNegocioException(String codigo, String mensaje) { super(mensaje); this.codigo = codigo; }
    public String getCodigo() { return codigo; }
}
```

## `ManejadorErroresWeb`

| Excepción | Petición | Respuesta |
|---|---|---|
| `ReglaNegocioException` con código `*_NO_ENCONTRADO`/`*_NO_ENCONTRADA` | cualquiera | vista `error` · HTTP 404 · `mensaje` = `getMessage()` |
| `ReglaNegocioException` (resto) | `POST` | `redirect:` al `Referer` (o a `/carrito` si no hay) con flash `error` = `getMessage()` |
| `ReglaNegocioException` (resto) | `GET` | vista `error` · HTTP 400 |
| `Exception` | cualquiera | vista `error` · HTTP 500 · "Ocurrió un error inesperado" (se registra en log con la traza completa) |

Solo conoce `ReglaNegocioException` (transversal), **nunca** las excepciones concretas del dominio (regla AR-03).

## `TrazaCapasAspect`

- `@Aspect @Component` en `transversal.traza`.
- Pointcut: métodos públicos de beans en `..presentacion..`, `..aplicacion..` y `..persistencia..`, **excluyendo** `..presentacion.sesion..` (los getters del carrito en sesión ensucian la traza).
- La capa se deduce del nombre del paquete de la clase objetivo: `presentacion` → `PRESENTACIÓN`, etc.
- Profundidad con `ThreadLocal<Integer>`: +1 al entrar, −1 en `finally`. Sangría = 4 espacios × profundidad.
- Argumentos resumidos: colecciones como `[n elementos]`, strings recortados a 30 caracteres, objetos con `toString()` recortado a 60.
- Nivel de log `INFO`, logger `pe.edu.ulima.tiendacapas.transversal`.

Formato esperado (ejemplo de la demo):

```
→ [PRESENTACIÓN] CheckoutController.confirmar(DatosClienteForm[...])
    → [APLICACIÓN] VentaService.registrarVenta(SolicitudVentaDTO[...])
        → [PERSISTENCIA] JdbcProductoDAO.buscarPorIds([2 elementos])
        ← [PERSISTENCIA] JdbcProductoDAO.buscarPorIds  3 ms
        → [PERSISTENCIA] JdbcProductoDAO.descontarStock(3, 1)
        ← [PERSISTENCIA] JdbcProductoDAO.descontarStock  1 ms
        → [PERSISTENCIA] JdbcVentaDAO.guardar(Venta[...])
        ← [PERSISTENCIA] JdbcVentaDAO.guardar  4 ms
    ← [APLICACIÓN] VentaService.registrarVenta  15 ms
← [PRESENTACIÓN] CheckoutController.confirmar  19 ms
```

El **dominio no aparece en la traza** a propósito: sus clases no son beans de Spring (Java puro), así que el aspecto no las intercepta. Esto se menciona en el video como evidencia de AR-05.

## `ArquitecturaCapasTest` (ArchUnit)

```java
@AnalyzeClasses(packages = "pe.edu.ulima.tiendacapas", importOptions = ImportOption.DoNotIncludeTests.class)
class ArquitecturaCapasTest {

    @ArchTest
    static final ArchRule capas = layeredArchitecture().consideringOnlyDependenciesInLayers()
        .layer("Presentacion").definedBy("..presentacion..")
        .layer("Aplicacion").definedBy("..aplicacion..")
        .layer("Dominio").definedBy("..dominio..")
        .layer("Persistencia").definedBy("..persistencia..")
        .layer("Transversal").definedBy("..transversal..")
        .whereLayer("Presentacion").mayNotBeAccessedByAnyLayer()                          // AR-01
        .whereLayer("Aplicacion").mayOnlyBeAccessedByLayers("Presentacion")              // AR-02
        .whereLayer("Dominio").mayOnlyBeAccessedByLayers("Aplicacion", "Persistencia")   // AR-03
        .whereLayer("Persistencia").mayNotBeAccessedByAnyLayer();                        // AR-04

    @ArchTest
    static final ArchRule dominioPuro = noClasses().that().resideInAPackage("..dominio..")
        .should().dependOnClassesThat().resideInAnyPackage(
            "org.springframework..", "java.sql..", "javax.sql..", "jakarta..");          // AR-05

    @ArchTest
    static final ArchRule transversalIndependiente = noClasses().that().resideInAPackage("..transversal..")
        .should().dependOnClassesThat().resideInAnyPackage(
            "..presentacion..", "..aplicacion..", "..dominio..", "..persistencia..");    // AR-06

    @ArchTest
    static final ArchRule sinCiclos = slices().matching("pe.edu.ulima.tiendacapas.(*)..")
        .should().beFreeOfCycles();                                                      // AR-07
}
```

Cada regla lleva `.because("…")` con la explicación en español, para que el mensaje de error sea legible en la demo.

## Esquema (`schema.sql`)

```sql
CREATE TABLE producto (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    sku         VARCHAR(20)   NOT NULL UNIQUE,
    nombre      VARCHAR(100)  NOT NULL,
    descripcion VARCHAR(500),
    categoria   VARCHAR(40)   NOT NULL,
    precio      DECIMAL(10,2) NOT NULL CHECK (precio > 0),
    stock       INT           NOT NULL CHECK (stock >= 0)
);

CREATE TABLE venta (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha           TIMESTAMP     NOT NULL,
    cliente_nombre  VARCHAR(80)   NOT NULL,
    cliente_email   VARCHAR(120)  NOT NULL,
    cliente_dni     CHAR(8)       NOT NULL,
    subtotal        DECIMAL(10,2) NOT NULL,
    descuento       DECIMAL(10,2) NOT NULL,
    base_imponible  DECIMAL(10,2) NOT NULL,
    igv             DECIMAL(10,2) NOT NULL,
    total           DECIMAL(10,2) NOT NULL
);

CREATE TABLE linea_venta (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    venta_id         BIGINT        NOT NULL REFERENCES venta(id),
    producto_id      BIGINT        NOT NULL REFERENCES producto(id),
    nombre_producto  VARCHAR(100)  NOT NULL,
    precio_unitario  DECIMAL(10,2) NOT NULL,
    cantidad         INT           NOT NULL CHECK (cantidad BETWEEN 1 AND 10),
    importe          DECIMAL(10,2) NOT NULL
);
```

El `CHECK (stock >= 0)` es una segunda barrera de RN-09: aunque el dominio y el `UPDATE` condicional ya la cumplen, la BD tampoco acepta stock negativo.

## Datos semilla (`data.sql` y `DatosSemillaMemoria`)

| id | sku | nombre | categoría | precio | stock | Para demostrar |
|---|---|---|---|---|---|---|
| 1 | LAP-001 | Laptop 14" Core i5 16 GB | Laptops | 2499.00 | 5 | Descuento por volumen (RN-05) |
| 2 | LAP-002 | Laptop 15.6" Ryzen 5 8 GB | Laptops | 1899.00 | 3 | — |
| 3 | MON-001 | Monitor 24" Full HD | Monitores | 549.00 | 4 | Un solo producto ya supera S/ 500 |
| 4 | ACC-001 | Mouse inalámbrico | Accesorios | 49.90 | 25 | Compra pequeña sin descuento |
| 5 | ACC-002 | Teclado mecánico | Accesorios | 159.00 | 10 | — |
| 6 | ACC-003 | Mochila para laptop 15.6" | Accesorios | 89.00 | **1** | Última unidad / stock insuficiente (RN-03, RN-09) |
| 7 | AUD-001 | Audífonos inalámbricos | Audio | 199.00 | 8 | — |
| 8 | AUD-002 | Parlante portátil | Audio | 149.00 | **0** | Agotado (RN-04) |

Descripciones: una frase corta por producto (las inventa quien implemente).

`DatosSemillaMemoria` devuelve la misma lista como objetos `Producto`; los DAO en memoria la copian al iniciar (cada arranque parte de cero, igual que H2).

## Perfiles

| Perfil | Beans de persistencia activos | `DataSource` / H2 |
|---|---|---|
| (ninguno) | `JdbcProductoDAO`, `JdbcVentaDAO` | Sí, con `schema.sql` y `data.sql` |
| `memoria` | `MemoriaProductoDAO`, `MemoriaVentaDAO` | No se usa (`spring.sql.init.mode=never`) |

`@Transactional` en los servicios sigue funcionando con el perfil `memoria` (el gestor de transacciones existe pero no afecta a las colecciones). Por eso `MemoriaProductoDAO.descontarStock` debe ser `synchronized` y la reversión ante fallo se resuelve en el servicio validando todo el stock **antes** de descontar (ver spec 03, diseño de `VentaService`).

## Pruebas de esta spec

| Prueba | Tipo | Verifica |
|---|---|---|
| `ArquitecturaCapasTest` | ArchUnit | BASE-3 (AR-01 a AR-07) |
| `TiendaCapasApplicationTests.contextLoads` | `@SpringBootTest` | BASE-1, BASE-4.1 |
| `PerfilMemoriaTest` | `@SpringBootTest @ActiveProfiles("memoria")` | BASE-4.2: el bean de `ProductoDAO` es `MemoriaProductoDAO` y lista 8 productos |
| `JdbcProductoDAOSemillaTest` | `@JdbcTest` + `@Import(JdbcProductoDAO.class)` | BASE-7.1 |
| `ManejadorErroresWebTest` | `@WebMvcTest` | BASE-6.3 a 6.5 con un controlador de prueba que lanza excepciones |
