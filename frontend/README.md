# frontend · Capa de Presentación (cliente)

Página que usa el vendedor en el navegador. Solo habla con el backend por HTTP (`/api/...`); nunca con la base de datos (AR-05).

| Archivo | Qué contiene |
|---|---|
| `index.html` | Formulario "Registrar venta" (producto, cantidad, cliente) y tabla de ventas |
| `app.js` | `fetch` a `GET /api/productos`, `GET /api/ventas` y `POST /api/ventas` |
| `estilos.css` | Estilos mínimos legibles en la grabación |
| `Dockerfile` | Imagen `tiendacapas-frontend:1.0` basada en nginx |
| `nginx.conf` | Sirve la página y reenvía `/api/` al contenedor del backend |

- **En contenedores** (`docker compose up -d --build` desde la raíz): nginx sirve esta carpeta en `http://localhost:8080` y reenvía `/api/...` al backend. `app.js` no cambia: siempre usa rutas relativas.
- **En desarrollo** (backend con `mvnw`): el propio backend sirve esta carpeta en `http://localhost:8080`.
