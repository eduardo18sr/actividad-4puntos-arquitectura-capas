# Spec 03 · Registrar venta — Requisitos

## Introducción

El comprador confirma su compra: ingresa sus datos, el sistema revalida el stock, descuenta las unidades y registra la venta **en una sola transacción**, y muestra el comprobante. Es el **flujo central de la demo** (Video 2): toca las cuatro capas, aplica casi todas las reglas de negocio y escribe en la base de datos.

Depende de: specs 00, 01 y 02.

## Requisitos

### VEN-1 · Formulario de checkout

**Historia**: Como comprador, quiero revisar mi pedido e ingresar mis datos, para confirmar la compra.

**Criterios de aceptación (EARS)**
1. CUANDO el comprador abre `GET /checkout` con un carrito listo para comprar, EL SISTEMA DEBE mostrar el resumen del pedido (líneas y totales) y un formulario con nombre, email y DNI.
2. SI el carrito está vacío o tiene alguna línea sin stock suficiente, ENTONCES EL SISTEMA DEBE redirigir a `/carrito` con el mensaje "Revisa tu carrito antes de continuar" (RN-07, CAR-5.1).

### VEN-2 · Validación de los datos del cliente

**Historia**: Como comprador, quiero que se me indique qué dato está mal, para corregirlo sin perder lo que escribí.

**Criterios de aceptación**
1. SI el nombre tiene menos de 3 o más de 80 caracteres, ENTONCES EL SISTEMA DEBE mostrar junto al campo "El nombre debe tener entre 3 y 80 caracteres" (RN-08).
2. SI el email no tiene formato válido, ENTONCES EL SISTEMA DEBE mostrar "Ingresa un email válido".
3. SI el DNI no tiene exactamente 8 dígitos, ENTONCES EL SISTEMA DEBE mostrar "El DNI debe tener 8 dígitos".
4. CUANDO hay errores de formato, EL SISTEMA DEBE volver a mostrar el formulario con los valores ingresados y **sin** llamar a la capa de aplicación.
5. AUNQUE la presentación ya haya validado el formato, EL SISTEMA DEBE volver a validar RN-08 en el dominio (`Cliente`): las reglas no pueden depender de que el cliente sea esta página web.

### VEN-3 · Registro transaccional de la venta

**Historia**: Como vendedor, quiero que cada venta confirmada quede registrada con totales correctos y el stock descontado, para no vender lo que no tengo.

**Criterios de aceptación**
1. CUANDO el comprador envía `POST /checkout` con datos válidos, EL SISTEMA DEBE, en una sola transacción: revalidar el stock de cada línea con datos actuales, descontar el stock de cada producto y guardar la venta con sus líneas y totales (RN-09).
2. EL SISTEMA DEBE calcular los totales en el dominio con las mismas reglas del carrito (RN-05, RN-06); el total registrado DEBE coincidir con el que se mostró en el checkout si nada cambió entre medio.
3. EL SISTEMA DEBE guardar en cada línea el nombre y el precio unitario vigentes al momento de la venta (RN-10).
4. SI algún producto ya no tiene stock suficiente al confirmar, ENTONCES EL SISTEMA DEBE rechazar **toda** la venta, no descontar el stock de **ningún** producto, no guardar nada y redirigir mostrando "Stock insuficiente para «nombre»: disponible n".
5. SI dos compradores confirman a la vez la última unidad de un producto, ENTONCES EL SISTEMA DEBE registrar solo una de las ventas; la otra DEBE rechazarse según VEN-3.4, y el stock final DEBE ser 0 (EC04).
6. SI el carrito llega vacío al servicio, ENTONCES EL SISTEMA DEBE rechazar la operación con "El carrito está vacío" (RN-07).

### VEN-4 · Comprobante

**Historia**: Como comprador, quiero ver el comprobante de mi compra, para tener constancia de lo que pagué.

**Criterios de aceptación**
1. CUANDO la venta se registra, EL SISTEMA DEBE vaciar el carrito de la sesión y redirigir a `GET /ventas/{id}` con el mensaje "¡Compra registrada con éxito!".
2. EL SISTEMA DEBE mostrar en el comprobante: número de venta (`V-000001`, RN-10), fecha y hora, datos del cliente, líneas (producto, precio unitario, cantidad, importe) y totales.
3. CUANDO el comprador recarga la página del comprobante, EL SISTEMA NO DEBE registrar otra venta (patrón Post/Redirect/Get).
4. SI el `id` no existe, ENTONCES EL SISTEMA DEBE responder 404 con "La venta solicitada no existe".
