# Steering · Producto

> Contexto permanente del proyecto. Todo agente o integrante que vaya a codear lee este archivo primero.

## Qué es

**TiendaCapas** es una página web de ventas acotada (una tienda en línea pequeña) construida para la **Parte C** de la actividad grupal de Ingeniería de Software II (2026-2): *Implementación y demostración en vivo* del estilo arquitectónico asignado, **Arquitectura en Capas**.

El objetivo no es construir un e-commerce completo, sino un sistema **lo bastante real para que las capas se noten**: una petición del navegador atraviesa presentación → aplicación → dominio → persistencia, y eso se puede ver, probar y explicar en vivo.

## Para qué existe (lo que el docente evalúa en la Parte C)

| # | Requisito de la Parte C | Cómo lo cubre este proyecto |
|---|---|---|
| 1 | Caso práctico acotado que evidencie el estilo | Tienda web: catálogo, carrito, registrar venta, consultar ventas |
| 2 | Diagrama de la arquitectura **realmente implementada** | `specs/arquitectura/arquitectura.md` (capas, C4, secuencia), alineado 1:1 con los paquetes Java |
| 3 | Aplicación funcional en una tecnología justificada | Java 21 + Spring Boot (ver `tecnologia.md`) |
| 4 | Ejecución en vivo explicando el recorrido de una funcionalidad | Flujo "Registrar venta" con traza por capa en consola (ver `specs/demo/guion-video2.md`) |
| 5 | Explicar **por qué** corresponde al estilo (no basta el nombre) | Reglas de dependencia verificadas con ArchUnit, cambio de persistencia sin tocar otras capas, pruebas del dominio sin BD |

## Actores

| Actor | Qué hace |
|---|---|
| **Comprador** | Navega el catálogo, arma su carrito y confirma la compra con sus datos |
| **Vendedor (admin)** | Consulta las ventas registradas y su detalle. Sin login (fuera de alcance) |

## Funcionalidades (una spec por funcionalidad)

| Spec | Funcionalidad | Prioridad |
|---|---|---|
| `00-fundaciones-capas` | Esqueleto del proyecto, paquetes por capa, reglas de dependencia (ArchUnit), traza transversal, perfiles de persistencia | Obligatoria — va primero |
| `01-catalogo` | Listar, buscar, filtrar por categoría y ver detalle de productos | Obligatoria |
| `02-carrito` | Agregar, cambiar cantidad, quitar y ver totales del carrito | Obligatoria |
| `03-registrar-venta` | Checkout: datos del cliente, validación de stock, registro transaccional, comprobante | Obligatoria — es el flujo de la demo |
| `04-consultar-ventas` | Lista y detalle de ventas registradas (vista del vendedor) | Obligatoria (sirve para probar en vivo que la venta se guardó) |

## Fuera de alcance (no construir)

- Login, registro de usuarios, roles y seguridad (Spring Security).
- Pagos reales, pasarelas, facturación electrónica, envíos.
- Administración de productos (CRUD de catálogo): los productos vienen de datos semilla.
- Despliegue en la nube, Docker, microservicios, colas de mensajes.
- Diseño visual elaborado: basta un CSS sencillo y legible en la grabación.

Si una idea no aparece en las specs, **no se construye** (YAGNI). Se discute primero y, si se aprueba, se agrega a la spec correspondiente.

## Reglas de negocio globales (fuente de verdad)

Estas reglas viven en la **capa de dominio** y las specs las referencian por su código.

| Código | Regla |
|---|---|
| **RN-01** | Los precios se expresan en soles (S/) con 2 decimales, usando `BigDecimal` y redondeo `HALF_UP`. Los precios del catálogo **no incluyen IGV**. |
| **RN-02** | Una línea del carrito tiene cantidad entre **1 y 10** unidades. |
| **RN-03** | La cantidad de un producto en el carrito nunca puede superar su **stock disponible**. |
| **RN-04** | Un producto con stock 0 se muestra como **"Agotado"** y no puede agregarse al carrito. |
| **RN-05** | **Descuento por volumen**: si el subtotal es **≥ S/ 500.00**, se aplica **5 %** de descuento sobre el subtotal. |
| **RN-06** | **IGV 18 %** sobre la base imponible (subtotal − descuento). Total = base imponible + IGV. |
| **RN-07** | No se puede registrar una venta con el carrito vacío. |
| **RN-08** | Datos del cliente: nombre de 3 a 80 caracteres, email con formato válido, DNI de exactamente 8 dígitos. |
| **RN-09** | Al confirmar la venta se **revalida el stock** y se **descuenta** en la misma transacción. Si algún producto ya no alcanza, la venta completa se rechaza y no se descuenta nada. El stock nunca queda negativo. |
| **RN-10** | Cada venta recibe un número correlativo con formato `V-000001`. Las líneas guardan nombre y precio unitario **al momento de la venta** (no cambian si luego cambia el catálogo). |

### Ejemplo de cálculo (para pruebas)

| Concepto | Valor |
|---|---|
| Subtotal (2 × S/ 300.00) | S/ 600.00 |
| Descuento 5 % (subtotal ≥ 500) | S/ 30.00 |
| Base imponible | S/ 570.00 |
| IGV 18 % | S/ 102.60 |
| **Total** | **S/ 672.60** |

## Glosario

| Término | Significado |
|---|---|
| **Capa** | Agrupación lógica de componentes con un mismo tipo de responsabilidad. En el código, un paquete Java de primer nivel. |
| **Capa cerrada** | Una capa solo puede usar la capa inmediatamente inferior (aquí: presentación → aplicación → dominio). |
| **Transversal** | Servicios que atraviesan todas las capas (traza, errores base). No depende de ninguna capa. |
| **DTO** | Objeto que cruza la frontera aplicación ↔ presentación. La presentación nunca ve objetos del dominio. |
| **DAO** | Objeto de acceso a datos. Su **interfaz** vive en el dominio; su **implementación** en persistencia. |
| **Carrito** | Selección temporal del comprador. Se guarda en la sesión HTTP; sus reglas (RN-02 a RN-06) están en el dominio. |
| **Venta** | Compra confirmada y persistida, con sus líneas y totales. |
| **Comprobante** | Página de confirmación que muestra el número y el detalle de la venta. |
