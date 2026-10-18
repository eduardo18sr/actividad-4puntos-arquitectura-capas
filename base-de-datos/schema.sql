-- BASE DE DATOS · Tablas de TiendaCapas (PostgreSQL).
-- El contenedor lo ejecuta la primera vez que se crea (ver docker-compose.yml).
-- Las pruebas del backend lo reutilizan sobre H2 en modo PostgreSQL.

CREATE TABLE producto (
    id      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre  VARCHAR(80)   NOT NULL,
    precio  NUMERIC(10,2) NOT NULL,
    stock   INT           NOT NULL CHECK (stock >= 0)
);

CREATE TABLE venta (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    producto_id BIGINT        NOT NULL REFERENCES producto(id),
    cliente     VARCHAR(80)   NOT NULL,
    cantidad    INT           NOT NULL,
    total       NUMERIC(10,2) NOT NULL,
    fecha       TIMESTAMP     NOT NULL
);
