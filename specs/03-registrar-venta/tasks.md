# Spec 03 · Registrar venta — Tareas

> Requiere las specs 00, 01 y 02. Es el flujo de la demo: al terminar, ensayar el guion de `specs/demo/guion-video2.md`.

- [ ] **3.1 Dominio: `Cliente` y `DatosClienteInvalidosException`**
  - `ClienteTest`.
  - _Requisitos: VEN-2.5 · RN-08_

- [ ] **3.2 Dominio: `LineaVenta`, `Venta`, `CarritoVacioException`, `VentaNoEncontradaException`**
  - `Venta.crear`, `reconstruir`, `conId`, `numero`. `VentaTest`.
  - _Requisitos: VEN-3.2, VEN-3.3, VEN-3.6, VEN-4.2 · RN-07, RN-10_

- [ ] **3.3 Persistencia: `descontarStock` en ambos DAO de producto**
  - `UPDATE` condicional en JDBC; `synchronized` en memoria. `JdbcProductoDAODescuentoTest`.
  - _Requisitos: VEN-3.1, VEN-3.4 · RN-09_

- [ ] **3.4 Persistencia: `JdbcVentaDAO` y `MemoriaVentaDAO` completos**
  - `guardar`, `buscarPorId`, `listar`. `JdbcVentaDAOTest`.
  - _Requisitos: VEN-3.1, VEN-3.3, VEN-4.2_

- [ ] **3.5 Aplicación: DTO, `VentaService` y `ConsultaVentasService.obtener`**
  - Pasos 1–8 de `design.md`, con `@Transactional`.
  - `VentaServiceTest` (sin Spring), `RegistrarVentaIntegracionTest` y `CompraSimultaneaTest` (con Spring y H2).
  - _Requisitos: VEN-3.1 a VEN-3.6, VEN-4.4 · EC04_

- [ ] **3.6 Presentación: `DatosClienteForm` y `CheckoutController`**
  - Rutas de `design.md`. `CheckoutControllerTest`.
  - _Requisitos: VEN-1, VEN-2.1 a VEN-2.4, VEN-4.1, VEN-4.3_

- [ ] **3.7 Presentación: plantillas `checkout/formulario.html` y `checkout/comprobante.html`**
  - _Requisitos: VEN-1.1, VEN-2.4, VEN-4.2_

- [ ] **3.8 Checkpoint de la spec 03**
  - A mano: comprar monitor + mouse, ver comprobante `V-000001`, confirmar en `/h2-console` que el stock bajó y que existen las filas en `venta` y `linea_venta`.
  - Abrir dos navegadores (uno en incógnito), poner la mochila (stock 1) en ambos carritos y confirmar casi a la vez: uno obtiene el comprobante y el otro el mensaje de stock insuficiente.
  - Revisar que la traza de consola muestre `PRESENTACIÓN → APLICACIÓN → PERSISTENCIA` para la compra.
  - Repetir la compra simple con el perfil `memoria`. `./mvnw test` en verde.
