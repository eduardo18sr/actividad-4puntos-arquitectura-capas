# CAPA DE PRESENTACIÓN (cliente) · Nginx sirve la página y reenvía /api/ al contenedor del backend.
FROM nginx:1.29-alpine

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY index.html app.js estilos.css /usr/share/nginx/html/

EXPOSE 80
