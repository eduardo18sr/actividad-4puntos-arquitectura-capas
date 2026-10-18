# Spec 04 · Consultar ventas — Requisitos

## Introducción

El vendedor ve las ventas registradas y abre el detalle de cada una. En la demo sirve para **probar en vivo que la venta quedó persistida** (sobre todo al cambiar al perfil `memoria`, donde no hay consola H2) y para mostrar una segunda lectura que atraviesa las capas.

Depende de: specs 00 y 03. Sin login (fuera de alcance).

## Requisitos

### CON-1 · Listar ventas

**Historia**: Como vendedor, quiero ver la lista de ventas registradas, para saber qué se ha vendido.

**Criterios de aceptación (EARS)**
1. CUANDO el vendedor abre `GET /admin/ventas`, EL SISTEMA DEBE mostrar todas las ventas, de la más reciente a la más antigua, con número, fecha y hora, nombre del cliente, unidades totales y total.
2. EL SISTEMA DEBE mostrar al pie la cantidad de ventas y la suma de sus totales ("3 ventas · S/ 1,234.56").
3. MIENTRAS no haya ventas, EL SISTEMA DEBE mostrar "Aún no hay ventas registradas".

### CON-2 · Ver detalle de una venta

**Historia**: Como vendedor, quiero abrir una venta, para ver qué se compró y a qué precio.

**Criterios de aceptación**
1. CUANDO el vendedor abre `GET /admin/ventas/{id}`, EL SISTEMA DEBE mostrar los mismos datos que el comprobante (VEN-4.2).
2. EL SISTEMA DEBE mostrar los precios **guardados en la venta**, aunque el precio del producto haya cambiado después (RN-10).
3. SI el `id` no existe, ENTONCES EL SISTEMA DEBE responder 404 con "La venta solicitada no existe".
