# Steering · Estructura y reglas de capas

> Estas reglas no se negocian. Si una tarea parece obligar a romper una, se detiene y se discute.

## Las capas

Variante: **arquitectura en 3 capas clásica** (presentación, negocio, datos) más la base de datos, con **capas cerradas**: cada capa solo usa la inmediatamente inferior.

| Capa | Dónde | Responsabilidad | Puede usar |
|---|---|---|---|
| **Presentación** | carpeta `frontend/` (HTML/JS) y paquete `presentacion` del backend (controlador REST) | Mostrar la pantalla, recibir la petición HTTP, devolver JSON y mensajes de error. **Sin reglas de negocio ni SQL.** | `negocio` |
| **Negocio** | paquete `negocio` | Aplicar RN-01 a RN-03, calcular el total y coordinar la transacción. **Sin HTTP ni SQL.** | `datos` |
| **Datos** | paquete `datos` | Ejecutar el SQL (`SELECT`, `INSERT`, `UPDATE`) y convertir filas en objetos. **Sin reglas de negocio.** | Base de datos (JDBC) |
| **Base de datos** | H2 · carpeta `base-de-datos/` (`schema.sql`, `data.sql`) | Guardar productos y ventas. | — |
| *Modelo (compartido)* | paquete `modelo` | Objetos simples (`Producto`, `Venta`) que viajan entre capas. Sin lógica. | — |

### Reglas de dependencia

| ID | Regla |
|---|---|
| **AR-01** | `presentacion` solo llama a `negocio` (nunca a `datos`). |
| **AR-02** | `negocio` solo llama a `datos` (nunca a `presentacion`). |
| **AR-03** | `datos` no llama a `negocio` ni a `presentacion`. |
| **AR-04** | El SQL solo aparece en `datos`. Las reglas RN solo aparecen en `negocio`. |
| **AR-05** | El frontend (`app.js`) solo habla con el backend por HTTP (`/api/...`); nunca con la BD. |
| **AR-06** | `modelo` no depende de ninguna capa; todas pueden usarlo. |

Se verifican en la revisión de código y se explican en el video abriendo los `import` de cada clase. Qué debe verse en cada una:

| Clase | Importa (permitido) | Nunca importa |
|---|---|---|
| `VentaController` | `VentaService` y `VentaException` (capa de negocio), `Producto` y `Venta` (`modelo`) | Nada de `datos` |
| `VentaService` | `ProductoRepository` y `VentaRepository` (capa de datos), `modelo` | Nada de `presentacion` |
| `ProductoRepository`, `VentaRepository` | `JdbcTemplate`, `modelo` | Nada de `negocio` ni de `presentacion` |

```
 index.html / app.js ──HTTP──► presentacion ──► negocio ──► datos ──JDBC──► H2
                                    │              │          │
                                    └──────────────┴──────────┴──► modelo (compartido)
```

## Árbol de archivos

El repositorio se divide en tres carpetas, una por pieza del sistema:

```
actividad-4puntos-arquitectura-capas/
├── frontend/                          ← CAPA DE PRESENTACIÓN (cliente)
│   ├── index.html                     (formulario + tabla de ventas)
│   ├── app.js                         (fetch a /api/...)
│   └── estilos.css
│
├── backend/                           ← Spring Boot (proyecto Maven)
│   ├── pom.xml, mvnw, .mvn/           (los genera start.spring.io en T1)
│   └── src/
│       ├── main/java/pe/edu/ulima/tiendacapas/
│       │   ├── TiendaCapasApplication.java
│       │   ├── presentacion/          ← CAPA DE PRESENTACIÓN (servidor)
│       │   │   ├── VentaController.java    (@RestController: /api/productos, /api/ventas)
│       │   │   └── VentaRequest.java       (record: datos que llegan del formulario)
│       │   ├── negocio/               ← CAPA DE NEGOCIO
│       │   │   ├── VentaService.java       (@Service: reglas RN-01..RN-03, @Transactional)
│       │   │   └── VentaException.java     (error de regla con mensaje para el usuario)
│       │   ├── datos/                 ← CAPA DE DATOS
│       │   │   ├── ProductoRepository.java (@Repository: SQL de la tabla producto)
│       │   │   └── VentaRepository.java    (@Repository: SQL de la tabla venta)
│       │   └── modelo/                ← compartido entre capas
│       │       ├── Producto.java           (record)
│       │       └── Venta.java              (record)
│       ├── main/resources/
│       │   └── application.properties      (BD H2 y consola; sin rutas a otras carpetas)
│       └── test/java/pe/edu/ulima/tiendacapas/
│           └── negocio/VentaServiceTest.java
│
├── base-de-datos/                     ← BASE DE DATOS (H2)
│   ├── schema.sql                     (CREATE TABLE producto, venta)
│   ├── data.sql                       (productos semilla)
│   └── consultas.sql                  (consultas para la demo en /h2-console)
│
└── specs/                             (SDD: steering, arquitectura, spec 01, guion)
```

- Al compilar, el `pom.xml` copia `frontend/` y `schema.sql`/`data.sql` **dentro** del backend (ver `tecnologia.md`). Por eso la app arranca igual desde cualquier carpeta, el `.jar` es un solo desplegable y todo corre con un solo comando.
- **Tres carpetas no son tres tiers.** Separan el código por pieza para repartir el trabajo, pero en ejecución todo corre en **un solo proceso** (la BD H2 vive en memoria dentro de la misma JVM). La separación que se demuestra es **lógica**.
- Las consultas que usa la aplicación (`SELECT`, `INSERT`, `UPDATE`) viven en `backend/.../datos/`, no en `base-de-datos/` (AR-04). `consultas.sql` es solo para mostrar en el video.

## Convenciones

- Nombres en español (`registrarVenta`, `descontarStock`).
- Inyección por constructor.
- Dinero con `BigDecimal` (escala 2). Nunca `double`.
- Cada clase lleva un comentario de una línea que diga **en qué capa está y qué hace** (ayuda en el video).
- Cada capa escribe una línea de log con su nombre al ser llamada, por ejemplo `[PRESENTACIÓN] POST /api/ventas`, `[NEGOCIO] registrarVenta`, `[DATOS] INSERT venta`. Así en la consola se ve el recorrido durante la demo.
