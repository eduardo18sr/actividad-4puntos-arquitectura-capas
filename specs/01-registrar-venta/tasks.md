# Spec 01 · Registrar venta — Tareas

Ejecutar en orden. Cada tarea termina con la app arrancando (`./mvnw spring-boot:run` desde `backend/`) y su casilla marcada `[x]`.

- [x] **T0 · Estructura de carpetas.** `frontend/`, `backend/` (paquetes `presentacion`, `negocio`, `datos`, `modelo`) y `base-de-datos/` con archivos vacíos, según `estructura.md`.
- [x] **T1 · Proyecto base.** Generar en start.spring.io con **Name `TiendaCapas`** y colocarlo en `backend/` siguiendo los pasos de `tecnologia.md` (copiar el contenido del zip, reemplazar los archivos vacíos y agregar el bloque `<resources>` al `pom.xml`). Copiar `application.properties`. _Listo cuando_: la app arranca.
- [x] **T2 · Base de datos.** Llenar `base-de-datos/schema.sql`, `data.sql` y `consultas.sql` del diseño. _Listo cuando_: `SELECT * FROM producto` devuelve 3 filas.
- [x] **T3 · Modelo.** Crear los records `Producto` y `Venta`.
- [x] **T4 · Capa de datos.** `ProductoRepository` y `VentaRepository` con el SQL del diseño y sus logs `[DATOS]`. _Requisitos_: VEN-1.2, VEN-2.2, VEN-2.3, VEN-4.1.
- [x] **T5 · Capa de negocio.** `VentaException` y `VentaService` con RN-01 a RN-03, `@Transactional` y logs `[NEGOCIO]`. _Requisitos_: VEN-2, VEN-3.
- [x] **T6 · Pruebas.** `VentaServiceTest` con las 4 pruebas del diseño. _Listo cuando_: `./mvnw test` en verde.
- [x] **T7 · Capa de presentación (backend).** `VentaRequest` y `VentaController` con los 3 endpoints, el manejador de `VentaException` y logs `[PRESENTACIÓN]`. _Listo cuando_: `GET http://localhost:8080/api/productos` devuelve JSON.
- [x] **T8 · Frontend.** `frontend/index.html`, `app.js`, `estilos.css`. _Requisitos_: VEN-1.1, VEN-2.4, VEN-3.5, VEN-4.1. _Listo cuando_: se registra una venta desde la página y aparece en la tabla.
- [x] **T10 · Base de datos en PostgreSQL (Docker).** contenedor PostgreSQL, `schema.sql` compatible con PostgreSQL, driver `postgresql` en el `pom.xml`, `application.properties` apuntando al contenedor y pruebas sobre H2 en modo PostgreSQL. _Listo cuando_: con `docker compose up -d` la app registra ventas en PostgreSQL, las ventas sobreviven a un reinicio del backend, 8 ventas simultáneas de Teclado (stock 5) dejan 5 registradas y stock 0, y `./mvnw test` pasa sin Docker.
- [x] **T11 · Imágenes Docker.** `frontend/Dockerfile` (nginx + `nginx.conf` que reenvía `/api/`), `backend/Dockerfile` (compila, prueba y ejecuta con JRE) y `base-de-datos/Dockerfile` (PostgreSQL + scripts), orquestados por `docker-compose.yml` en la raíz. _Listo cuando_: `docker compose up -d --build` levanta los 3 contenedores, una venta desde `http://localhost:8080` recorre nginx → presentación → negocio → datos → PostgreSQL (visible en `docker compose logs backend`), el backend no se publica en la PC y los datos sobreviven a `docker compose down` + `up`.
- [ ] **T9 · Prueba manual de la demo.** Seguir `specs/demo/guion-video2.md` de principio a fin: venta válida (Mouse × 3 = S/ 75.00), venta con stock insuficiente (Monitor × 3), venta con cantidad 0; revisar la consola (`[PRESENTACIÓN] → [NEGOCIO] → [DATOS]`) y la tabla `venta` con `psql` (ver `base-de-datos/consultas.sql`).
