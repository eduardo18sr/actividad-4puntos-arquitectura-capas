# Spec 00 · Fundaciones de las capas — Requisitos

## Introducción

Antes de cualquier funcionalidad de negocio se construye el **esqueleto en capas**: proyecto ejecutable, paquetes por capa, reglas de dependencia verificadas automáticamente, persistencia intercambiable, traza transversal, manejo central de errores y datos semilla. Esta spec es la que convierte "usamos capas" en algo **demostrable**.

Contexto: `specs/steering/*.md` y `specs/arquitectura/arquitectura.md` (MA01–MA04, EC01–EC03, D01–D09).

## Requisitos

### BASE-1 · Proyecto ejecutable con un comando

**Historia**: Como integrante del grupo, quiero arrancar la aplicación con un solo comando, para hacer la demo en vivo en cualquier laptop sin instalar nada más que Java (RA04).

**Criterios de aceptación (EARS)**
1. CUANDO se ejecuta `./mvnw spring-boot:run` (o `.\mvnw.cmd spring-boot:run` en Windows), EL SISTEMA DEBE iniciar en el puerto 8080 en menos de 15 segundos.
2. CUANDO el usuario abre `http://localhost:8080/`, EL SISTEMA DEBE redirigir a `/productos`.
3. EL SISTEMA DEBE funcionar sin conexión a internet ni servicios externos.

### BASE-2 · Un paquete por capa

**Historia**: Como evaluador, quiero ver en el árbol del proyecto una capa por paquete, para relacionar el diagrama con el código.

**Criterios de aceptación**
1. EL SISTEMA DEBE organizar el código bajo `pe.edu.ulima.tiendacapas` en exactamente cinco paquetes de primer nivel: `presentacion`, `aplicacion`, `dominio`, `persistencia`, `transversal` (más la clase `TiendaCapasApplication`).
2. EL SISTEMA DEBE ubicar cada clase en la capa que indica `specs/steering/estructura.md`.

### BASE-3 · Reglas de dependencia verificadas automáticamente

**Historia**: Como grupo, queremos que una violación de las capas rompa el build, para demostrar que el estilo se cumple y no solo se nombra (MA04, EC03).

**Criterios de aceptación**
1. CUANDO se ejecuta `./mvnw test`, EL SISTEMA DEBE ejecutar `ArquitecturaCapasTest`, que verifica las reglas AR-01 a AR-07 de `estructura.md`.
2. SI una clase de `presentacion` depende de una clase de `persistencia` o de `dominio`, ENTONCES la prueba DEBE fallar con un mensaje que nombre la clase y la regla violada.
3. SI una clase de `dominio` importa algo de `org.springframework`, `java.sql`, `javax.sql` o `jakarta`, ENTONCES la prueba DEBE fallar.
4. CUANDO el código respeta todas las reglas, la prueba DEBE pasar.

### BASE-4 · Persistencia intercambiable por perfil

**Historia**: Como grupo, queremos cambiar la implementación de persistencia sin tocar otras capas, para demostrar en vivo la modificabilidad que aportan las capas (MA02, EC01).

**Criterios de aceptación**
1. MIENTRAS no haya perfil activo, EL SISTEMA DEBE usar `JdbcProductoDAO` y `JdbcVentaDAO` sobre H2.
2. MIENTRAS el perfil `memoria` esté activo, EL SISTEMA DEBE usar `MemoriaProductoDAO` y `MemoriaVentaDAO`, con los mismos datos semilla.
3. EL SISTEMA DEBE comportarse igual con ambos perfiles en todas las funcionalidades (01 a 04).
4. Las clases de `presentacion`, `aplicacion` y `dominio` NO DEBEN contener ninguna referencia a perfiles ni a clases concretas de persistencia.

### BASE-5 · Traza transversal del recorrido por capas

**Historia**: Como expositor del video, quiero ver en la consola por qué capas pasa cada petición, para explicar el recorrido de una funcionalidad mientras la ejecuto.

**Criterios de aceptación**
1. CUANDO se invoca un método público de un componente Spring de `presentacion`, `aplicacion` o `persistencia`, EL SISTEMA DEBE escribir en el log una línea de entrada con la capa, la clase, el método y los argumentos resumidos.
2. CUANDO ese método termina, EL SISTEMA DEBE escribir una línea de salida con la duración en milisegundos (o el nombre de la excepción, si falló).
3. EL SISTEMA DEBE sangrar cada línea según la profundidad de la llamada, para que el anidamiento presentación → aplicación → persistencia se lea a simple vista.
4. EL SISTEMA NO DEBE requerir cambios en el código de las capas para producir la traza (todo vive en `transversal`).

### BASE-6 · Manejo central de errores de negocio

**Historia**: Como comprador, quiero ver un mensaje claro cuando una regla de negocio me impide hacer algo, en vez de una página de error técnica.

**Criterios de aceptación**
1. EL SISTEMA DEBE definir `ReglaNegocioException` (abstracta) en `transversal.error`, con un `codigo` y un mensaje en español.
2. Todas las excepciones del dominio DEBEN extender `ReglaNegocioException`.
3. SI una `ReglaNegocioException` llega a la presentación desde una petición `POST`, ENTONCES EL SISTEMA DEBE redirigir a la página de origen mostrando el mensaje como alerta.
4. SI la excepción es de tipo "no encontrado" (`codigo` termina en `_NO_ENCONTRADO`/`_NO_ENCONTRADA`), ENTONCES EL SISTEMA DEBE responder con la página `error.html` y estado HTTP 404.
5. SI ocurre una excepción no prevista, ENTONCES EL SISTEMA DEBE mostrar `error.html` con estado 500 y un mensaje genérico, sin traza técnica en pantalla.

### BASE-7 · Esquema y datos semilla

**Historia**: Como expositor, quiero que la tienda arranque siempre con los mismos productos, para que la demo sea reproducible.

**Criterios de aceptación**
1. CUANDO la aplicación inicia con el perfil por defecto, EL SISTEMA DEBE crear las tablas `producto`, `venta` y `linea_venta` y cargar los 8 productos semilla.
2. CUANDO la aplicación inicia con el perfil `memoria`, EL SISTEMA DEBE cargar los mismos 8 productos en memoria.
3. EL SISTEMA DEBE incluir en la semilla al menos: un producto **agotado** (stock 0), un producto con **stock 1** y un producto de precio **≥ S/ 500**, para demostrar RN-04, RN-09 y RN-05.
