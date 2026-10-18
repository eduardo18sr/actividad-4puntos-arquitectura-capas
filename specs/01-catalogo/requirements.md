# Spec 01 · Catálogo — Requisitos

## Introducción

El comprador ve los productos de la tienda, los busca por texto, los filtra por categoría y abre su detalle. Es la funcionalidad más simple y sirve para mostrar el recorrido de una **lectura** por las capas: controlador → `CatalogoService` → `ProductoDAO` → BD, con el resultado convertido a `ProductoDTO` antes de llegar a la vista.

Depende de: spec 00.

## Requisitos

### CAT-1 · Listar productos

**Historia**: Como comprador, quiero ver todos los productos con su precio y disponibilidad, para decidir qué comprar.

**Criterios de aceptación (EARS)**
1. CUANDO el comprador abre `GET /productos`, EL SISTEMA DEBE mostrar todos los productos ordenados por nombre, cada uno con nombre, categoría, precio en formato `S/ 1,899.00` y estado de disponibilidad.
2. MIENTRAS un producto tenga stock 0, EL SISTEMA DEBE mostrarlo con la etiqueta **"Agotado"** y sin botón "Agregar al carrito" (RN-04).
3. MIENTRAS un producto tenga stock entre 1 y 3, EL SISTEMA DEBE mostrar "¡Últimas n unidades!".
4. MIENTRAS un producto tenga stock mayor que 3, EL SISTEMA DEBE mostrar "Disponible".

### CAT-2 · Buscar por texto

**Historia**: Como comprador, quiero buscar por nombre, para encontrar rápido lo que necesito.

**Criterios de aceptación**
1. CUANDO el comprador envía `GET /productos?q=<texto>`, EL SISTEMA DEBE mostrar solo los productos cuyo nombre o descripción contengan el texto, sin distinguir mayúsculas de minúsculas.
2. SI la búsqueda no encuentra productos, ENTONCES EL SISTEMA DEBE mostrar "No se encontraron productos para «texto»" y un enlace para ver todo el catálogo.
3. SI `q` está vacío o solo tiene espacios, ENTONCES EL SISTEMA DEBE tratarlo como "sin filtro".

### CAT-3 · Filtrar por categoría

**Historia**: Como comprador, quiero filtrar por categoría, para ver solo lo que me interesa.

**Criterios de aceptación**
1. EL SISTEMA DEBE mostrar en la página del catálogo la lista de categorías existentes (obtenidas de los datos, no escritas en la plantilla).
2. CUANDO el comprador elige una categoría (`GET /productos?categoria=<nombre>`), EL SISTEMA DEBE mostrar solo los productos de esa categoría y marcarla como activa.
3. CUANDO se envían `q` y `categoria` juntos, EL SISTEMA DEBE aplicar ambos filtros.

### CAT-4 · Ver detalle de un producto

**Historia**: Como comprador, quiero ver el detalle de un producto, para conocer su descripción y elegir cuántas unidades comprar.

**Criterios de aceptación**
1. CUANDO el comprador abre `GET /productos/{id}`, EL SISTEMA DEBE mostrar SKU, nombre, descripción, categoría, precio y disponibilidad.
2. MIENTRAS el producto tenga stock, EL SISTEMA DEBE mostrar un formulario con cantidad (por defecto 1, mínimo 1, máximo `min(10, stock)`) y el botón "Agregar al carrito" que envía a `POST /carrito/items` (spec 02).
3. SI el `id` no existe, ENTONCES EL SISTEMA DEBE mostrar la página de error con estado 404 y el mensaje "El producto solicitado no existe".

### CAT-5 · La presentación no conoce el dominio

**Historia**: Como grupo, queremos que las vistas reciban solo DTO, para respetar la regla de capas cerradas (AR-03).

**Criterios de aceptación**
1. EL SISTEMA DEBE entregar a los controladores y plantillas únicamente objetos `ProductoDTO` (nunca `Producto`).
2. EL SISTEMA DEBE calcular el texto de disponibilidad (`Agotado` / `¡Últimas n unidades!` / `Disponible`) fuera de la plantilla: la regla de cuándo un producto está agotado vive en el dominio (`Producto.estaAgotado()`), y el servicio la traduce al DTO.
