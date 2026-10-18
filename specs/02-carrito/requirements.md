# Spec 02 · Carrito — Requisitos

## Introducción

El comprador arma su carrito: agrega productos, cambia cantidades, quita líneas y ve los totales con descuento e IGV. El carrito se guarda en la **sesión HTTP** (estado de la interfaz, capa de presentación), pero **todas sus reglas** (cantidades, stock, descuento, IGV) se ejecutan en el **dominio**, a través de un servicio de aplicación sin estado (decisión D05).

Depende de: specs 00 y 01.

## Requisitos

### CAR-1 · Agregar un producto

**Historia**: Como comprador, quiero agregar un producto con la cantidad que elija, para comprarlo después.

**Criterios de aceptación (EARS)**
1. CUANDO el comprador envía `POST /carrito/items` con `productoId` y `cantidad`, EL SISTEMA DEBE agregar la línea al carrito y redirigir a `/carrito` con el mensaje "Producto agregado al carrito".
2. CUANDO el producto ya está en el carrito, EL SISTEMA DEBE **sumar** la cantidad a la línea existente (no crear otra línea).
3. SI la cantidad resultante de la línea es menor que 1 o mayor que 10, ENTONCES EL SISTEMA DEBE rechazar la operación con "La cantidad por producto debe estar entre 1 y 10" (RN-02).
4. SI la cantidad resultante supera el stock, ENTONCES EL SISTEMA DEBE rechazar la operación con "Stock insuficiente para «nombre»: disponible n" (RN-03).
5. SI el producto está agotado, ENTONCES EL SISTEMA DEBE rechazar la operación con "«nombre» está agotado" (RN-04).
6. SI el producto no existe, ENTONCES EL SISTEMA DEBE responder 404 (BASE-6.4).
7. CUANDO se rechaza una operación, EL SISTEMA DEBE dejar el carrito exactamente como estaba.

### CAR-2 · Ver el carrito con totales

**Historia**: Como comprador, quiero ver mi carrito con el detalle de lo que pagaré, para decidir si compro.

**Criterios de aceptación**
1. CUANDO el comprador abre `GET /carrito`, EL SISTEMA DEBE mostrar cada línea con producto, precio unitario, cantidad e importe (precio × cantidad).
2. EL SISTEMA DEBE mostrar subtotal, descuento, base imponible, IGV (18 %) y total calculados según RN-05 y RN-06.
3. MIENTRAS el subtotal sea menor que S/ 500.00, EL SISTEMA DEBE mostrar "Te faltan S/ x para obtener 5 % de descuento".
4. MIENTRAS el carrito esté vacío, EL SISTEMA DEBE mostrar "Tu carrito está vacío", un enlace al catálogo y ningún botón para continuar la compra.
5. EL SISTEMA DEBE mostrar en la cabecera de todas las páginas la cantidad total de unidades del carrito: "Carrito (n)".
6. CUANDO se muestra el carrito, EL SISTEMA DEBE usar los **precios y stock actuales** del catálogo (no los del momento en que se agregó).

### CAR-3 · Cambiar la cantidad de una línea

**Criterios de aceptación**
1. CUANDO el comprador envía `POST /carrito/items/{productoId}/cantidad` con una nueva `cantidad`, EL SISTEMA DEBE reemplazar la cantidad de esa línea y redirigir a `/carrito`.
2. Las reglas CAR-1.3, CAR-1.4 y CAR-1.7 DEBEN aplicarse igual.

### CAR-4 · Quitar una línea y vaciar el carrito

**Criterios de aceptación**
1. CUANDO el comprador envía `POST /carrito/items/{productoId}/eliminar`, EL SISTEMA DEBE quitar esa línea y redirigir a `/carrito` con "Producto quitado del carrito".
2. CUANDO el comprador envía `POST /carrito/vaciar`, EL SISTEMA DEBE dejar el carrito vacío.

### CAR-5 · Coherencia con el catálogo

**Criterios de aceptación**
1. SI al mostrar el carrito un producto ya no tiene stock suficiente para la cantidad guardada (por ejemplo, porque otro comprador lo compró), ENTONCES EL SISTEMA DEBE mostrar la línea marcada con "Stock insuficiente: disponible n" y deshabilitar "Continuar compra" hasta que el comprador la corrija.
2. SI un producto guardado en la sesión ya no existe, ENTONCES EL SISTEMA DEBE retirarlo del carrito en silencio.
