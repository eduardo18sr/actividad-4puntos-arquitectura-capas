# Steering · Producto

> Contexto permanente del proyecto. Se lee antes de codear.

## Qué es

**TiendaCapas** es una aplicación web mínima para la **Parte C** de la actividad grupal de Ingeniería de Software II (2026-2): demostrar en vivo el estilo **Arquitectura en Capas**.

Hace **una sola cosa: registrar una venta**. El usuario elige un producto, escribe la cantidad y el nombre del cliente, y pulsa "Registrar venta". El sistema valida, calcula el total, descuenta el stock y guarda la venta. Debajo del formulario se ve la tabla de ventas registradas (para comprobar en vivo que se guardó).

Es deliberadamente pequeño: el objetivo es que **las capas se noten**, no construir una tienda.

## Cómo cubre la Parte C

| # | Requisito de la Parte C | Cómo se cubre |
|---|---|---|
| 1 | Caso práctico acotado | Registrar una venta |
| 2 | Diagrama de la arquitectura realmente implementada | `specs/arquitectura/arquitectura.md`, alineado 1:1 con las clases |
| 3 | Aplicación funcional con tecnología justificada | Java 21 + Spring Boot + H2 (ver `tecnologia.md`) |
| 4 | Ejecución en vivo explicando el recorrido | Registrar una venta y seguir la petición por las capas (log en consola con el nombre de cada capa) |
| 5 | Explicar por qué es Arquitectura en Capas | Cada capa en su paquete, con una responsabilidad y llamando solo a la de abajo (ver `arquitectura.md`, sección 4) |

## Actor

| Actor | Qué hace |
|---|---|
| **Vendedor** | Registra ventas desde la página y ve la lista de ventas registradas. Sin login. |

## Alcance

**Dentro**: listar productos en un desplegable, registrar una venta, listar ventas registradas.

**Fuera (no construir)**: login, carrito, varios productos por venta, IGV/descuentos, pagos, editar o anular ventas, CRUD de productos (vienen de datos semilla), Docker, nube, pruebas de arquitectura automáticas, diseño visual elaborado.

## Reglas de negocio (viven en la capa de negocio)

| Código | Regla | Mensaje si se incumple |
|---|---|---|
| **RN-01** | La cantidad debe ser un entero **≥ 1** y el cliente no puede estar vacío. | "La cantidad debe ser al menos 1" / "Ingresa el nombre del cliente" |
| **RN-02** | No se puede vender más que el **stock** disponible. Al registrar la venta, el stock **se descuenta**. | "Stock insuficiente: disponible n" |
| **RN-03** | El **total** lo calcula el backend: `precio del producto × cantidad`, en soles con 2 decimales (`BigDecimal`). El frontend nunca envía el total. | — |

### Ejemplo (para probar)

Producto "Mouse" a S/ 25.00 con stock 10. Vender 3 → total **S/ 75.00**, stock queda en **7**. Intentar vender 8 después → "Stock insuficiente: disponible 7".

## Glosario

| Término | Significado |
|---|---|
| **Capa** | Grupo de clases con un mismo tipo de responsabilidad. En el código, un paquete Java. |
| **Frontend** | La página `index.html` + `app.js` que corre en el navegador. |
| **Backend** | La aplicación Spring Boot (capas presentación, negocio y datos). |
| **Repositorio** | Clase de la capa de datos que contiene el SQL de una tabla. |
