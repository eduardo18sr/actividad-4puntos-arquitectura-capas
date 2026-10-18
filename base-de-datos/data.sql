-- BASE DE DATOS · Productos semilla. El contenedor los inserta al crearse
-- (para volver a este estado: docker compose down -v && docker compose up -d).

INSERT INTO producto (nombre, precio, stock) VALUES ('Mouse', 25.00, 10);
INSERT INTO producto (nombre, precio, stock) VALUES ('Teclado', 80.00, 5);
INSERT INTO producto (nombre, precio, stock) VALUES ('Monitor 24"', 450.00, 2);
