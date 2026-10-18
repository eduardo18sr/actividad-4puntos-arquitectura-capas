# Spec 01 · Catálogo — Tareas

> Requiere la spec 00 terminada. Orden: de abajo hacia arriba (dominio → persistencia → aplicación → presentación).

- [ ] **1.1 Dominio: reglas de `Producto` y `ProductoNoEncontradoException`**
  - Completar `Producto` (validaciones, `estaAgotado`, `hayStockPara`, `maximoComprable`).
  - `ProductoTest` sin Spring.
  - _Requisitos: CAT-1.2, CAT-5.2 · RN-02, RN-03, RN-04_

- [ ] **1.2 Persistencia: filtros de `listar` y `listarCategorias` en ambos DAO**
  - SQL de `design.md` en `JdbcProductoDAO`; equivalente con streams en `MemoriaProductoDAO`.
  - `JdbcProductoDAOTest` (`@JdbcTest`).
  - _Requisitos: CAT-2.1, CAT-3.1, CAT-3.3_

- [ ] **1.3 Aplicación: `ProductoDTO` y `CatalogoService`**
  - `ProductoDTO.desde(Producto)` con el texto de disponibilidad.
  - `CatalogoServiceTest` instanciando el servicio con `new CatalogoService(new MemoriaProductoDAO(...))`.
  - _Requisitos: CAT-1.1 a CAT-1.4, CAT-2.3, CAT-4.3, CAT-5.1, CAT-5.2_

- [ ] **1.4 Presentación: `CatalogoController` y plantillas**
  - `catalogo/lista.html` y `catalogo/detalle.html` según `design.md`.
  - `CatalogoControllerTest` (`@WebMvcTest`).
  - _Requisitos: CAT-1 a CAT-4_

- [ ] **1.5 Checkpoint de la spec 01**
  - Probar a mano: buscar "laptop", filtrar "Accesorios", abrir el parlante (agotado, sin botón), abrir `/productos/999` (404).
  - Verificar en consola la traza `PRESENTACIÓN → APLICACIÓN → PERSISTENCIA` al abrir el catálogo.
  - Repetir con el perfil `memoria`. `./mvnw test` en verde (incluye `ArquitecturaCapasTest`).
