# Spec 04 · Consultar ventas — Tareas

> Requiere las specs 00 y 03.

- [ ] **4.1 Aplicación: `VentaListadoDTO`, `ListadoVentasDTO` y `ConsultaVentasService.listar`**
  - `ConsultaVentasServiceTest` con `MemoriaVentaDAO`.
  - _Requisitos: CON-1.1 a CON-1.3_

- [ ] **4.2 Presentación: `VentasAdminController`, fragmento `detalle-venta.html` y plantillas**
  - Refactorizar `checkout/comprobante.html` para usar el fragmento compartido.
  - `VentasAdminControllerTest`.
  - _Requisitos: CON-1, CON-2_

- [ ] **4.3 Checkpoint final del sistema**
  - Hacer 2 compras y verlas en `/admin/ventas` con la suma correcta.
  - Repetir con el perfil `memoria`.
  - `./mvnw test` en verde (todas las specs + `ArquitecturaCapasTest`).
  - Revisar que `specs/arquitectura/arquitectura.md` y `specs/steering/estructura.md` coincidan con el código final (nombres de clases, paquetes, rutas). Si no, actualizar los documentos.
  - Ensayar completo el guion `specs/demo/guion-video2.md`.
