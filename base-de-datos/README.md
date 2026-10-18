# base-de-datos · PostgreSQL en Docker

La base de datos corre en un contenedor PostgreSQL. Al crearse por primera vez ejecuta `schema.sql` y `data.sql` (ver `specs/steering/tecnologia.md`).

```bash
docker compose up -d      # levantar (desde esta carpeta)
docker compose stop       # detener conservando los datos
docker compose down -v    # borrar todo; al volver a levantar se recrean tablas y semilla
```

Consola: `docker exec -it tiendacapas-db psql -U tienda -d tienda`

Si el puerto 5432 ya está ocupado (por ejemplo, por un PostgreSQL instalado en la PC), levantar con otro puerto (`DB_PUERTO=5433`) y arrancar el backend con `DB_URL=jdbc:postgresql://localhost:5433/tienda`. Comandos exactos en el `README.md` de la raíz.

| Archivo | Qué contiene | Lo usa |
|---|---|---|
| `docker-compose.yml` | Contenedor `tiendacapas-db` (PostgreSQL 17, puerto 5432 o `DB_PUERTO`, volumen `datos-tienda`) | `docker compose` |
| `schema.sql` | `CREATE TABLE producto` y `CREATE TABLE venta` | Contenedor al crearse; pruebas del backend (H2) |
| `data.sql` | Productos semilla (Mouse, Teclado, Monitor 24") | Contenedor al crearse; pruebas del backend (H2) |
| `consultas.sql` | Consultas para mostrar en la demo desde `psql` | Equipo, a mano |

Las consultas que usa la aplicación (`SELECT`, `INSERT`, `UPDATE`) **no** están aquí: viven en la capa de datos del backend (`ProductoRepository`, `VentaRepository`), según AR-04.
