# Steering · Estructura y reglas de capas

> Estas reglas no se negocian. Si una tarea parece obligar a romper una, se detiene y se discute.

## Las capas

Variante: **arquitectura en 3 capas clásica** (presentación, negocio, datos) más la base de datos, con **capas cerradas**: cada capa solo usa la inmediatamente inferior.

| Capa | Dónde | Responsabilidad | Puede usar |
|---|---|---|---|
| **Presentación** | `static/index.html`, `static/app.js` (frontend) y paquete `presentacion` (controlador REST) | Mostrar la pantalla, recibir la petición HTTP, devolver JSON y mensajes de error. **Sin reglas de negocio ni SQL.** | `negocio` |
| **Negocio** | paquete `negocio` | Aplicar RN-01 a RN-03, calcular el total y coordinar la transacción. **Sin HTTP ni SQL.** | `datos` |
| **Datos** | paquete `datos` | Ejecutar el SQL (`SELECT`, `INSERT`, `UPDATE`) y convertir filas en objetos. **Sin reglas de negocio.** | Base de datos (JDBC) |
| **Base de datos** | H2 (`schema.sql`, `data.sql`) | Guardar productos y ventas. | — |
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

Se verifican en la revisión de código y se explican en el video abriendo los `import` de cada clase.

```
 index.html / app.js ──HTTP──► presentacion ──► negocio ──► datos ──JDBC──► H2
                                    │              │          │
                                    └──────────────┴──────────┴──► modelo (compartido)
```

## Árbol de archivos

```
src/main/java/pe/edu/ulima/tiendacapas/
├── TiendaCapasApplication.java
├── presentacion/
│   ├── VentaController.java        (@RestController: /api/productos, /api/ventas)
│   └── VentaRequest.java           (record: datos que llegan del formulario)
├── negocio/
│   ├── VentaService.java           (@Service: reglas RN-01..RN-03, @Transactional)
│   └── VentaException.java         (error de regla de negocio con mensaje para el usuario)
├── datos/
│   ├── ProductoRepository.java     (@Repository: SQL de la tabla producto)
│   └── VentaRepository.java        (@Repository: SQL de la tabla venta)
└── modelo/
    ├── Producto.java               (record)
    └── Venta.java                  (record)

src/main/resources/
├── application.properties
├── schema.sql
├── data.sql
└── static/
    ├── index.html
    ├── app.js
    └── estilos.css

src/test/java/pe/edu/ulima/tiendacapas/
└── negocio/VentaServiceTest.java
```

## Convenciones

- Nombres en español (`registrarVenta`, `descontarStock`).
- Inyección por constructor.
- Dinero con `BigDecimal` (escala 2). Nunca `double`.
- Cada clase lleva un comentario de una línea que diga **en qué capa está y qué hace** (ayuda en el video).
- Cada capa escribe una línea de log con su nombre al ser llamada, por ejemplo `[PRESENTACIÓN] POST /api/ventas`, `[NEGOCIO] registrarVenta`, `[DATOS] INSERT venta`. Así en la consola se ve el recorrido durante la demo.
