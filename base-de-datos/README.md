# base-de-datos · PostgreSQL en Docker

La imagen `tiendacapas-db:1.0` (`Dockerfile`, basada en `postgres:17-alpine`) lleva dentro `schema.sql` y `data.sql`: al crearse el contenedor por primera vez crea las tablas y los productos semilla. La levanta el `docker-compose.yml` de la raíz:

```bash
docker compose up -d db      # solo la BD (desde la raíz del repo)
docker compose stop          # detener conservando los datos
docker compose down -v       # borrar todo; al volver a levantar se recrean tablas y semilla
```

Consola: `docker exec -it tiendacapas-db psql -U tienda -d tienda`

| Archivo | Qué contiene | Lo usa |
|---|---|---|
| `Dockerfile` | Imagen `tiendacapas-db:1.0`: PostgreSQL 17 + `schema.sql` y `data.sql` en `/docker-entrypoint-initdb.d` | `docker compose` (raíz) |
| `schema.sql` | `CREATE TABLE producto` y `CREATE TABLE venta` | Contenedor al crearse; pruebas del backend (H2) |
| `data.sql` | Productos semilla (Mouse, Teclado, Monitor 24") | Contenedor al crearse; pruebas del backend (H2) |
| `consultas.sql` | Consultas para mostrar en la demo desde `psql` | Equipo, a mano |

Las consultas que usa la aplicación (`SELECT`, `INSERT`, `UPDATE`) **no** están aquí: viven en la capa de datos del backend (`ProductoRepository`, `VentaRepository`), según AR-04.
