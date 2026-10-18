# base-de-datos · H2

Scripts SQL de la base de datos. El backend los ejecuta al arrancar (ver `specs/steering/tecnologia.md`), así la BD se recrea igual en cada demo.

| Archivo | Qué contendrá | Lo usa |
|---|---|---|
| `schema.sql` | `CREATE TABLE producto` y `CREATE TABLE venta` | Backend al arrancar |
| `data.sql` | Productos semilla (Mouse, Teclado, Monitor 24") | Backend al arrancar |
| `consultas.sql` | Consultas para mostrar en la demo desde `/h2-console` | Equipo, a mano |

Las consultas que usa la aplicación (`SELECT`, `INSERT`, `UPDATE`) **no** están aquí: viven en la capa de datos del backend (`ProductoRepository`, `VentaRepository`), según AR-04.
