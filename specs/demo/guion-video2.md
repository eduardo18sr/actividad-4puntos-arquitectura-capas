# Guion · Video 2 (Implementación y demostración funcional)

> Objetivo: mostrar la app **ejecutándose de verdad** y probar que es Arquitectura en Capas por su comportamiento, no por los nombres. **Todos** los integrantes hablan. Duración sugerida: 10–12 min.
> Reemplazar "Integrante A/B/C/D" por los nombres reales y repartir según el tamaño del grupo.

## Antes de grabar (checklist)

- [ ] `./mvnw test` en verde la misma mañana de la grabación.
- [ ] Reiniciar la app justo antes de grabar (los datos semilla se recrean; la primera venta será `V-000001`).
- [ ] IDE y terminal con fuente grande (≥ 16 pt); consola con la traza visible al lado del navegador.
- [ ] Dos navegadores listos (uno normal y uno en incógnito) para el caso de compra simultánea.
- [ ] Diagrama de capas (`specs/arquitectura/arquitectura.md`, sección 5) abierto o en la diapositiva.
- [ ] Rama limpia (`git status` sin cambios) para poder deshacer la "violación" del paso 5 con `git checkout -- .`.
- [ ] Audio probado: el docente debe entender cada explicación.

## Recorrido

| # | Tiempo | Quién | Qué se muestra | Qué se dice (ideas clave) | Requisito Parte C |
|---|---|---|---|---|---|
| 1 | 0:00–1:30 | Integrante A | Diapositiva con el **diagrama de capas** implementado y la tabla de tecnologías | Caso acotado: tienda web. Cuatro capas + transversal. Capas cerradas: cada una solo usa la de abajo. El dominio en el centro: la persistencia implementa sus interfaces. Por qué Java + Spring + Thymeleaf + JDBC (`tecnologia.md`). | C1, C2, C3 |
| 2 | 1:30–3:30 | Integrante B | En el IDE: árbol de paquetes; abrir **una clase por capa** del flujo de compra: `CheckoutController` → `VentaService` → `Venta`/`Totales` → `JdbcProductoDAO` | El controlador no calcula nada, solo valida formato y redirige. El servicio coordina y abre la transacción. Las reglas (descuento, IGV, stock) están en el dominio, que **no importa Spring ni SQL** (mostrar los imports de `Venta`). El DAO solo tiene SQL. | C2, C5 |
| 3 | 3:30–6:00 | Integrante C | **App en vivo**: `./mvnw spring-boot:run`. Catálogo → buscar "monitor" → agregar **Monitor 24"** → agregar **Mouse** → carrito → checkout → datos → confirmar → comprobante | Mientras se navega, señalar la **traza en consola**: `PRESENTACIÓN → APLICACIÓN → PERSISTENCIA`, con sangría y tiempos. Explicar el descuento: subtotal S/ 598.90 ≥ 500 → descuento S/ 29.95, base S/ 568.95, IGV S/ 102.41, **total S/ 671.36**. Nota: el dominio no aparece en la traza porque es Java puro, no un bean de Spring. | C4 |
| 4 | 6:00–6:45 | Integrante C | `/admin/ventas` y `/h2-console`: `SELECT * FROM producto` (stock del monitor bajó de 4 a 3) y `SELECT * FROM linea_venta` | La venta se registró en una sola transacción; las líneas guardan el precio del momento. | C4 |
| 5 | 6:45–8:00 | Integrante D | **Regla de negocio que atraviesa capas**: en los dos navegadores, poner la **Mochila** (stock 1) en el carrito y confirmar en ambos, uno tras otro | Uno obtiene el comprobante; el otro, "Stock insuficiente". El dominio revalida y el `UPDATE … WHERE stock >= ?` de persistencia impide stock negativo; la transacción revierte todo. La presentación solo muestra el mensaje. | C4, C5 |
| 6 | 8:00–9:30 | Integrante D | **Las capas se verifican solas**: `./mvnw test -Dtest=ArquitecturaCapasTest` en verde. Luego, en `CatalogoController`, agregar un campo `private JdbcProductoDAO atajo;` (debe **usar** la clase: un `import` sin uso no cuenta), volver a correr → **rojo** con el mensaje de la regla. Deshacer con `git checkout -- .` | Esta es la diferencia entre "llamar capas a unos paquetes" y aplicar el estilo: la regla de dependencia es una prueba automática que rompe el build. | C5 |
| 7 | 9:30–11:00 | Integrante A o B | **Reemplazar la capa de persistencia en vivo**: detener la app, arrancar con `./mvnw spring-boot:run -Dspring-boot.run.profiles=memoria`, repetir una compra y verla en `/admin/ventas`. En la traza aparece `MemoriaProductoDAO` en vez de `JdbcProductoDAO` | Se cambió toda la persistencia **sin tocar una línea** de presentación, aplicación ni dominio (EC01). Spring inyecta otra implementación de la misma interfaz del dominio. | C5 |
| 8 | 11:00–12:00 | Todos (una idea cada uno) | Diapositiva de cierre | Trade-offs reales que vivimos: más clases y mapeos DTO; algunos métodos de servicio solo "pasan" la llamada; todo se despliega junto (si se quisiera escalar por separado, eso ya sería multi-tier o microservicios). A cambio: responsabilidades claras, pruebas sin BD y cambios localizados. | C5 |

## Comandos de la demo (copiar y pegar)

Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

```powershell
.\mvnw.cmd test "-Dtest=ArquitecturaCapasTest"
```

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=memoria"
```

Consultas para la consola H2 (`http://localhost:8080/h2-console`, JDBC URL `jdbc:h2:mem:tiendacapas`, usuario `sa`):

```sql
SELECT id, nombre, stock FROM producto ORDER BY id;
SELECT * FROM venta;
SELECT * FROM linea_venta;
```

## Preguntas que pueden hacer y respuesta corta

| Pregunta | Respuesta |
|---|---|
| ¿Por qué no MVC? | Usamos MVC **dentro** de la capa de presentación (Spring MVC). MVC organiza la interfaz; las capas organizan todo el sistema. Se complementan. |
| ¿No es multi-tier? | No: todo corre en un proceso. Las capas son lógicas. Pasar H2 a un servidor de BD aparte lo volvería multi-tier, y solo cambiaría persistencia y configuración. |
| ¿Por qué la persistencia apunta al dominio? | Inversión de dependencias: el negocio define qué necesita (`ProductoDAO`) y la persistencia lo cumple. Así cambiar de JDBC a memoria (o a otra BD) no toca el negocio. |
| ¿Por qué validan dos veces el DNI? | La presentación valida formato para dar buena experiencia; el dominio valida la regla porque no puede depender de que el único cliente sea esta página. |
| ¿Por qué no JPA? | Con JPA las clases del dominio llevan anotaciones de persistencia; preferimos un dominio puro y un DAO con SQL visible para que cada capa se vea claramente. |
| ¿Qué pasa con el rendimiento? | Atravesar capas agrega llamadas en memoria; en la traza se ve que una compra completa toma unos milisegundos (EC05). |
