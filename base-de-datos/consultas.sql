-- Consultas para la demo. Abrir la consola de PostgreSQL del contenedor:
--   docker exec -it tiendacapas-db psql -U tienda -d tienda
-- La aplicación NO usa este archivo: su SQL vive en la capa de datos del backend.

-- Productos y su stock actual
SELECT * FROM producto ORDER BY id;

-- Ventas registradas (la más reciente primero)
SELECT * FROM venta ORDER BY id DESC;

-- Ventas con el nombre del producto
SELECT v.id, p.nombre, v.cliente, v.cantidad, v.total, v.fecha
FROM venta v JOIN producto p ON p.id = v.producto_id
ORDER BY v.id DESC;
