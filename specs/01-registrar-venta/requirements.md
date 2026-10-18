# Spec 01 · Registrar venta — Requisitos

## Introducción

Única funcionalidad del sistema. El vendedor elige un producto, indica la cantidad y el cliente, y registra la venta. El backend valida, calcula el total, descuenta el stock y guarda. La lista de ventas se muestra en la misma página para comprobar que se guardó.

Reglas de negocio: RN-01 a RN-03 (`specs/steering/producto.md`).

## Requisitos

### VEN-1 · Ver productos

**Historia**: Como vendedor, quiero ver los productos disponibles con su precio y stock, para elegir qué vender.

**Criterios de aceptación (EARS)**
1. CUANDO se abre `http://localhost:8080`, EL SISTEMA DEBE mostrar un desplegable con los productos de la BD en el formato `Nombre — S/ precio (stock n)`.
2. EL SISTEMA DEBE obtener los productos con `GET /api/productos`.

### VEN-2 · Registrar venta

**Historia**: Como vendedor, quiero registrar una venta, para que quede guardada y el stock se actualice.

**Criterios de aceptación**
1. CUANDO el vendedor envía el formulario con datos válidos, EL SISTEMA DEBE llamar a `POST /api/ventas` con `{productoId, cliente, cantidad}`.
2. EL SISTEMA DEBE calcular el total en el backend como `precio × cantidad` (RN-03) y guardarlo en la tabla `venta` con la fecha y hora actual.
3. EL SISTEMA DEBE descontar la cantidad del stock del producto (RN-02).
4. CUANDO la venta se registra, EL SISTEMA DEBE responder `201` con la venta creada y la página DEBE mostrar "Venta registrada: S/ total", actualizar la tabla de ventas y el stock del desplegable.

### VEN-3 · Rechazar ventas inválidas

**Historia**: Como vendedor, quiero que el sistema me avise si la venta no es válida, para corregirla.

**Criterios de aceptación**
1. SI la cantidad es menor que 1, ENTONCES EL SISTEMA DEBE responder `400` con "La cantidad debe ser al menos 1" (RN-01).
2. SI el cliente está vacío, ENTONCES EL SISTEMA DEBE responder `400` con "Ingresa el nombre del cliente" (RN-01).
3. SI la cantidad supera el stock, ENTONCES EL SISTEMA DEBE responder `400` con "Stock insuficiente: disponible n" (RN-02).
4. SI el producto no existe, ENTONCES EL SISTEMA DEBE responder `400` con "El producto no existe".
5. CUANDO la venta se rechaza, EL SISTEMA NO DEBE guardar la venta ni cambiar el stock, y la página DEBE mostrar el mensaje en rojo.
6. SI el nombre del cliente supera 80 caracteres, ENTONCES EL SISTEMA DEBE responder `400` con "El nombre del cliente no puede superar 80 caracteres" (RN-01).
7. SI dos ventas del mismo producto llegan al mismo tiempo y entre ambas superan el stock, ENTONCES EL SISTEMA DEBE registrar solo la que alcance y rechazar la otra con "Stock insuficiente: disponible n"; el stock NUNCA DEBE quedar negativo (RN-02).

### VEN-4 · Ver ventas registradas

**Historia**: Como vendedor, quiero ver las ventas registradas, para comprobar que se guardaron.

**Criterios de aceptación**
1. EL SISTEMA DEBE mostrar debajo del formulario una tabla con N°, fecha, cliente, producto, cantidad y total, obtenida con `GET /api/ventas`, de la más reciente a la más antigua.
