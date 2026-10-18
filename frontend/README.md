# frontend · Capa de Presentación (cliente)

Página que usa el vendedor en el navegador. Solo habla con el backend por HTTP (`/api/...`); nunca con la base de datos (AR-05).

| Archivo | Qué contendrá |
|---|---|
| `index.html` | Formulario "Registrar venta" (producto, cantidad, cliente) y tabla de ventas |
| `app.js` | `fetch` a `GET /api/productos`, `GET /api/ventas` y `POST /api/ventas` |
| `estilos.css` | Estilos mínimos legibles en la grabación |

El backend sirve esta carpeta en `http://localhost:8080` (ver `specs/steering/tecnologia.md`), así no hace falta un servidor aparte.
