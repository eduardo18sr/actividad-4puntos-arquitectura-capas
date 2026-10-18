# Guion · Video 2 (Implementación y demostración funcional)

> Objetivo: mostrar la app **ejecutándose de verdad** y explicar por qué es Arquitectura en Capas. **Todos** los integrantes hablan. Duración sugerida: 6–8 min. Reemplazar "Integrante A/B/C/D" por los nombres reales.

## Antes de grabar

- [ ] `./mvnw test` en verde.
- [ ] Reiniciar la app justo antes de grabar (los datos semilla se recrean).
- [ ] IDE y navegador con letra grande; consola visible al lado del navegador.
- [ ] Diagrama de capas (`specs/arquitectura/arquitectura.md`, sección 2) en la diapositiva.

## Recorrido

| # | Tiempo | Quién | Qué se muestra | Qué se dice |
|---|---|---|---|---|
| 1 | 0:00–1:30 | Integrante A | Diapositiva con el **diagrama de capas** y la tabla de tecnologías | Caso acotado: registrar una venta. Frontend (HTML/JS) → Presentación (controlador) → Negocio (servicio) → Datos (repositorios) → BD H2. Capas cerradas: cada una solo usa la de abajo. Por qué Spring Boot, JS simple y H2. |
| 2 | 1:30–3:30 | Integrante B | En el IDE, **una clase por capa**: `app.js` → `VentaController` → `VentaService` → `ProductoRepository` / `VentaRepository` → `schema.sql` | El controlador no decide nada, solo recibe y responde. Las reglas (cantidad, stock, total) están en el servicio. El SQL solo está en los repositorios. Mostrar los `import`: cada clase solo conoce la capa de abajo. |
| 3 | 3:30–5:00 | Integrante C | **App en vivo**: `./mvnw spring-boot:run`, abrir `localhost:8080`. Registrar **Mouse × 3, cliente "Ana"** | Señalar en la consola: `[PRESENTACIÓN] POST /api/ventas` → `[NEGOCIO] registrarVenta` → `[DATOS] SELECT / UPDATE / INSERT`. Total S/ 75.00 calculado en el backend; stock del Mouse baja de 10 a 7. |
| 4 | 5:00–6:00 | Integrante D | Registrar **Monitor × 3** (stock 2) y luego **cantidad 0** | La capa de negocio rechaza la venta ("Stock insuficiente: disponible 2", "La cantidad debe ser al menos 1"); la presentación solo muestra el mensaje; la capa de datos no llega a escribir nada. |
| 5 | 6:00–6:45 | Integrante D | `/h2-console`: `SELECT * FROM venta;` y `SELECT * FROM producto;` | La venta quedó guardada en la BD y el stock actualizado; las rechazadas no dejaron rastro (transacción). |
| 6 | 6:45–8:00 | Todos (una idea cada uno) | Diapositiva de cierre | A favor: responsabilidades claras, fácil de dividir y probar, cambiar la BD o el frontend no toca el negocio. En contra: llamadas que solo "pasan" por una capa y todo se despliega junto. |

## Comandos (Windows PowerShell)

```powershell
.\mvnw.cmd spring-boot:run
```

```powershell
.\mvnw.cmd test
```

Consola H2: `http://localhost:8080/h2-console` · JDBC URL `jdbc:h2:mem:tienda` · usuario `sa` · sin contraseña.

```sql
SELECT * FROM producto;
SELECT * FROM venta;
```

## Preguntas posibles

| Pregunta | Respuesta corta |
|---|---|
| ¿No es MVC? | MVC organiza solo la interfaz; las capas organizan todo el sistema (presentación, negocio, datos). Se complementan. |
| ¿No es Multi-Tier? | No: el backend corre en un solo proceso y las capas son lógicas. Separar físicamente la BD o el frontend en otros servidores sería la vista de tiers. |
| ¿Por qué el total lo calcula el backend? | Es una regla de negocio (RN-03): vive en la capa de negocio y no puede depender de lo que envíe la página. |
| ¿Qué pasa si cambiamos de base de datos? | Solo cambian `application.properties` y, si hiciera falta, el SQL de la capa de datos. Negocio y presentación no se tocan. |
