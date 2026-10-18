# Guion · Video 2 (Implementación y demostración funcional)

> Objetivo: mostrar la app **ejecutándose de verdad** y explicar por qué es Arquitectura en Capas. **Todos** los integrantes hablan. Duración sugerida: 6–8 min. Reemplazar "Integrante A/B/C/D" por los nombres reales.

## Antes de grabar

- [ ] `./mvnw clean test` en verde.
- [ ] Dejar todo en los datos semilla justo antes de grabar, desde la raíz: `docker compose down -v` y `docker compose up -d --build`.
- [ ] IDE y navegador con letra grande; consola visible al lado del navegador.
- [ ] Diagrama de capas (`specs/arquitectura/arquitectura.md`, sección 2) en la diapositiva.

## Recorrido

| # | Tiempo | Quién | Qué se muestra | Qué se dice |
|---|---|---|---|---|
| 1 | 0:00–1:30 | Integrante A | Diapositiva con el **diagrama de capas** y la tabla de tecnologías | Caso acotado: registrar una venta. Frontend (HTML/JS) → Presentación (controlador) → Negocio (servicio) → Datos (repositorios) → PostgreSQL en Docker. Capas cerradas: cada una solo usa la de abajo. Por qué Spring Boot, JS simple, PostgreSQL y Docker. |
| 2 | 1:30–3:30 | Integrante B | En el IDE, **una clase por capa**: `app.js` → `VentaController` → `VentaService` → `ProductoRepository` / `VentaRepository` → `base-de-datos/schema.sql` | El controlador no decide nada, solo recibe y responde. Las reglas (cantidad, stock, total) están en el servicio. El SQL solo está en los repositorios. Mostrar los `import`: cada clase solo conoce la capa de abajo (y `modelo`, que es compartido). |
| 3 | 3:30–5:00 | Integrante C | **App en vivo**: `docker compose ps` (los 3 contenedores arriba, BD `healthy`), `docker compose logs -f backend` en una terminal visible, abrir `localhost:8080`. Registrar **Mouse × 3, cliente "Ana"** | Señalar en la consola: `[PRESENTACIÓN] POST /api/ventas` → `[NEGOCIO] registrarVenta` → `[DATOS] SELECT / UPDATE / INSERT`. Total S/ 75.00 calculado en el backend; stock del Mouse baja de 10 a 7. |
| 4 | 5:00–6:00 | Integrante D | Registrar **Monitor × 3** (stock 2) y luego **cantidad 0** | La capa de negocio rechaza la venta ("Stock insuficiente: disponible 2", "La cantidad debe ser al menos 1"); la presentación solo muestra el mensaje; la capa de datos no llega a escribir nada. |
| 5 | 6:00–6:45 | Integrante D | `docker exec -it tiendacapas-db psql -U tienda -d tienda` y las consultas de `base-de-datos/consultas.sql` | La venta quedó guardada en la BD y el stock actualizado. Las rechazadas no dejaron rastro porque la capa de negocio valida **antes** de escribir; además `@Transactional` asegura que, si algo fallara a mitad del registro, tampoco quede nada a medias. |
| 6 | 6:45–8:00 | Todos (una idea cada uno) | Diapositiva de cierre | A favor: responsabilidades claras, fácil de dividir y probar, cambiar la BD o el frontend no toca el negocio. En contra: llamadas que solo "pasan" por una capa y todo se despliega junto. |

## Comandos (Windows PowerShell)

```powershell
docker compose up -d --build
```

```powershell
docker compose logs -f backend
```

```powershell
docker compose ps
```

Pruebas (desde `backend/`):

```powershell
.\mvnw.cmd clean test
```

```powershell
.\mvnw.cmd test
```

Consola de la BD: `docker exec -it tiendacapas-db psql -U tienda -d tienda`

Las consultas para la demo están en `base-de-datos/consultas.sql`.

## Preguntas posibles

| Pregunta | Respuesta corta |
|---|---|
| ¿No es MVC? | MVC organiza solo la interfaz; las capas organizan todo el sistema (presentación, negocio, datos). Se complementan. |
| ¿No es Multi-Tier? | Lo que demostramos es la separación **lógica**: presentación, negocio y datos en capas cerradas, visible en los paquetes y sus `import`. Los contenedores son la vista física (despliegue) y solo facilitan ejecutarlo; podríamos correr todo en un proceso (`mvnw spring-boot:run`) sin cambiar una línea. |
| ¿Por qué el total lo calcula el backend? | Es una regla de negocio (RN-03): vive en la capa de negocio y no puede depender de lo que envíe la página. |
| ¿Tres contenedores no son tres tiers? | Son tres piezas desplegadas por separado, sí; pero el estilo está en cómo se organiza y se llama el código: cada capa solo usa la de abajo. El backend tiene tres capas dentro de un mismo contenedor, y la regla se cumple igual que si todo corriera junto. |
| ¿Y si dos personas venden el último producto a la vez? | El `UPDATE … WHERE stock >= ?` de la capa de datos descuenta solo si alcanza; la capa de negocio rechaza la otra venta. El stock nunca queda negativo. |
| ¿Qué pasa si cambiamos de base de datos? | Ya lo hicimos: pasamos de H2 a PostgreSQL cambiando solo configuración (`application.properties`, `pom.xml`) y `schema.sql`. Ninguna clase de presentación, negocio ni datos cambió. |
| ¿Por qué Docker? | Para que todo se levante igual en cualquier máquina con un comando (`docker compose up -d --build`), con las mismas versiones, tablas y datos semilla. |
