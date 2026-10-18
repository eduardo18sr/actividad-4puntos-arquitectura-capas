# Steering · Estructura y reglas de capas

> Estas reglas **no se negocian** durante la implementación. Si una tarea parece obligar a romper una, se detiene y se discute en el equipo.

## Las capas

Variante usada: **N-capas orientada al dominio** (la vista en la Semana 7), simplificada a cuatro capas más una transversal. No hay capa de "servicios distribuidos" porque hay un solo cliente (el navegador) y un solo desplegable.

| Capa | Paquete | Responsabilidad | Puede depender de |
|---|---|---|---|
| **Presentación** | `presentacion` | Recibir peticiones HTTP, validar formato de formularios, guardar el carrito en sesión, elegir la vista y mostrar mensajes. Sin reglas de negocio. | `aplicacion`, `transversal` |
| **Aplicación** | `aplicacion` | Un servicio por caso de uso: coordina, abre la transacción, convierte DTO ↔ dominio. Sin SQL ni HTML. Sin estado propio. | `dominio`, `transversal` |
| **Dominio** | `dominio` | Modelo y reglas de negocio (RN-01 a RN-10), excepciones de negocio y **contratos** (interfaces) de los DAO. Java puro: sin Spring, sin JDBC. | `transversal` (solo la excepción base) |
| **Persistencia** | `persistencia` | Implementaciones de los DAO: JDBC (SQL) y en memoria. Traduce filas ↔ objetos del dominio. Sin reglas de negocio. | `dominio`, `transversal` |
| **Transversal** | `transversal` | Traza de capas (aspecto AOP) y excepción base `ReglaNegocioException`. | **Ninguna** capa |

### Sentido de las dependencias

```
              Presentación
                   │  usa (DTO)
                   ▼
               Aplicación
                   │  usa (modelo + interfaces DAO)
                   ▼
                Dominio  ◄──────── implementa ──────── Persistencia
                                                          (JDBC / memoria)

   Transversal: la pueden usar todas; ella no usa a ninguna.
```

- En **tiempo de ejecución** la llamada baja: presentación → aplicación → (dominio) → persistencia → BD.
- En **código** la persistencia apunta **hacia el dominio**: implementa `ProductoDAO` y `VentaDAO`, que el dominio define. Es inversión de dependencias (DIP): el negocio no depende del detalle técnico que más cambia.

### Reglas verificadas por `ArquitecturaCapasTest` (ArchUnit)

| ID | Regla |
|---|---|
| **AR-01** | Ninguna capa depende de `presentacion`. |
| **AR-02** | Solo `presentacion` depende de `aplicacion`. |
| **AR-03** | Solo `aplicacion` y `persistencia` dependen de `dominio` (la presentación **no** ve el dominio). |
| **AR-04** | Ninguna capa depende de `persistencia` (las implementaciones las inyecta Spring). |
| **AR-05** | `dominio` no depende de `org.springframework..`, `java.sql..`, `javax.sql..` ni `jakarta..`. |
| **AR-06** | `transversal` no depende de `presentacion`, `aplicacion`, `dominio` ni `persistencia`. |
| **AR-07** | No hay ciclos entre los subpaquetes de primer nivel. |

## Árbol de paquetes

```
src/main/java/pe/edu/ulima/tiendacapas/
├── TiendaCapasApplication.java
├── presentacion/
│   ├── web/
│   │   ├── InicioController.java           (GET / → /productos)
│   │   ├── CatalogoController.java
│   │   ├── CarritoController.java
│   │   ├── CheckoutController.java
│   │   └── VentasAdminController.java
│   ├── sesion/
│   │   └── CarritoSesion.java              (@SessionScope: productoId → cantidad)
│   ├── formulario/
│   │   └── DatosClienteForm.java           (Bean Validation)
│   └── error/
│       └── ManejadorErroresWeb.java        (@ControllerAdvice)
├── aplicacion/
│   ├── CatalogoService.java
│   ├── CarritoService.java
│   ├── VentaService.java
│   ├── ConsultaVentasService.java
│   └── dto/
│       ├── ProductoDTO.java
│       ├── ItemCarritoDTO.java
│       ├── LineaResumenDTO.java
│       ├── ResumenCarritoDTO.java
│       ├── SolicitudVentaDTO.java
│       ├── ComprobanteDTO.java
│       ├── LineaVentaDTO.java
│       ├── VentaListadoDTO.java
│       └── ListadoVentasDTO.java
├── dominio/
│   ├── modelo/
│   │   ├── Producto.java
│   │   ├── Carrito.java
│   │   ├── LineaCarrito.java
│   │   ├── Totales.java
│   │   ├── Cliente.java
│   │   ├── Venta.java
│   │   └── LineaVenta.java
│   ├── excepcion/
│   │   ├── ProductoNoEncontradoException.java
│   │   ├── StockInsuficienteException.java
│   │   ├── ProductoAgotadoException.java
│   │   ├── CantidadInvalidaException.java
│   │   ├── CarritoVacioException.java
│   │   ├── DatosClienteInvalidosException.java
│   │   └── VentaNoEncontradaException.java
│   └── repositorio/
│       ├── ProductoDAO.java                (interface)
│       └── VentaDAO.java                   (interface)
├── persistencia/
│   ├── jdbc/
│   │   ├── JdbcProductoDAO.java            (@Repository @Profile("!memoria"))
│   │   └── JdbcVentaDAO.java
│   └── memoria/
│       ├── MemoriaProductoDAO.java         (@Repository @Profile("memoria"))
│       ├── MemoriaVentaDAO.java
│       └── DatosSemillaMemoria.java
└── transversal/
    ├── error/
    │   └── ReglaNegocioException.java      (abstracta, Java puro)
    └── traza/
        └── TrazaCapasAspect.java           (@Aspect)

src/main/resources/
├── application.properties
├── application-memoria.properties
├── schema.sql
├── data.sql
├── static/css/estilos.css
└── templates/
    ├── fragmentos/layout.html
    ├── fragmentos/detalle-venta.html
    ├── catalogo/lista.html
    ├── catalogo/detalle.html
    ├── carrito/ver.html
    ├── checkout/formulario.html
    ├── checkout/comprobante.html
    ├── admin/ventas.html
    ├── admin/venta-detalle.html
    └── error.html

src/test/java/pe/edu/ulima/tiendacapas/
├── arquitectura/ArquitecturaCapasTest.java
├── dominio/   (pruebas unitarias puras: sin Spring, sin BD)
├── aplicacion/ (servicios con DAO en memoria, sin Spring)
├── persistencia/ (DAO JDBC contra H2: @JdbcTest)
└── presentacion/ (controladores: @WebMvcTest con servicios simulados)
```

## Qué cruza cada frontera

| Frontera | Lo que cruza | Lo que **no** cruza |
|---|---|---|
| Navegador ↔ Presentación | Parámetros de formulario, HTML | — |
| Presentación ↔ Aplicación | **DTO** (`record`) y `ReglaNegocioException` | Objetos del dominio (`Producto`, `Venta`…) |
| Aplicación ↔ Dominio | Objetos del dominio, interfaces DAO | SQL, `HttpSession`, DTO |
| Dominio ↔ Persistencia | Objetos del dominio | `ResultSet`, SQL (quedan dentro del DAO) |

## Convenciones de código

- Nombres en **español**, siguiendo el estilo del curso (`VentaService`, `ProductoDAO`, `registrarVenta`).
- DTO como `record` en `aplicacion.dto`. El mapeo dominio → DTO lo hace el servicio (método estático `desde(...)` en el DTO).
- Inyección **por constructor** (sin `@Autowired` en campos).
- Dinero siempre `BigDecimal` con escala 2 y `RoundingMode.HALF_UP` (RN-01). Nunca `double`.
- Los servicios de aplicación son clases concretas (`@Service`), sin interfaz: la abstracción se pone en la frontera que cambia (los DAO), no en todas partes.
- Las excepciones de negocio extienden `ReglaNegocioException` (con un `codigo` corto, p. ej. `STOCK_INSUFICIENTE`, y un mensaje en español apto para el usuario).
- Los controladores aplican **Post/Redirect/Get**: todo `POST` termina en `redirect:` con mensajes en `RedirectAttributes` (flash).
- Cada clase pública de dominio y aplicación lleva un Javadoc de una línea que diga **en qué capa está y qué responsabilidad tiene** (ayuda a explicar en el video).

## Definición de terminado (para cada tarea)

1. El código está en el paquete de la capa que indica el `design.md` de la spec.
2. `./mvnw test` pasa, **incluido `ArquitecturaCapasTest`**.
3. Los criterios de aceptación que la tarea cita tienen prueba o verificación manual anotada.
4. La casilla de la tarea se marca `[x]` en el `tasks.md` correspondiente.
