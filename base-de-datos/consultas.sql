-- Consultas para la demo en http://localhost:8080/h2-console
-- (JDBC URL jdbc:h2:mem:tienda · usuario sa · sin contraseña).
-- La aplicación NO usa este archivo: su SQL vive en la capa de datos del backend.

-- Productos y su stock actual
SELECT * FROM producto ORDER BY id;

-- Ventas registradas (la más reciente primero)
SELECT * FROM venta ORDER BY id DESC;

-- Ventas con el nombre del producto
SELECT v.id, p.nombre, v.cliente, v.cantidad, v.total, v.fecha
FROM venta v JOIN producto p ON p.id = v.producto_id
ORDER BY v.id DESC;
