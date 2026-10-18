-- BASE DE DATOS · Tablas de TiendaCapas (H2). El backend ejecuta este script al arrancar.

CREATE TABLE producto (
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre  VARCHAR(80)   NOT NULL,
    precio  DECIMAL(10,2) NOT NULL,
    stock   INT           NOT NULL CHECK (stock >= 0)
);

CREATE TABLE venta (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    producto_id BIGINT        NOT NULL REFERENCES producto(id),
    cliente     VARCHAR(80)   NOT NULL,
    cantidad    INT           NOT NULL,
    total       DECIMAL(10,2) NOT NULL,
    fecha       TIMESTAMP     NOT NULL
);
