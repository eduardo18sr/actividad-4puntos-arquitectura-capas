# BASE DE DATOS · Imagen PostgreSQL con las tablas y los datos semilla de TiendaCapas.
# Al crearse el contenedor por primera vez (volumen vacío) ejecuta los scripts en orden: tablas y luego semilla.
FROM postgres:17-alpine

ENV POSTGRES_DB=tienda \
    POSTGRES_USER=tienda \
    POSTGRES_PASSWORD=tienda \
    TZ=America/Lima \
    PGTZ=America/Lima

COPY schema.sql /docker-entrypoint-initdb.d/01-schema.sql
COPY data.sql   /docker-entrypoint-initdb.d/02-data.sql

HEALTHCHECK --interval=5s --timeout=3s --retries=10 \
    CMD pg_isready -U "$POSTGRES_USER" -d "$POSTGRES_DB" || exit 1
