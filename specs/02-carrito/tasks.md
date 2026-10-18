# Spec 02 · Carrito — Tareas

> Requiere las specs 00 y 01.

- [ ] **2.1 Dominio: `Totales`**
  - `Totales.calcular` y `faltaParaDescuento`. `TotalesTest` parametrizada con la tabla de `design.md`.
  - _Requisitos: CAR-2.2, CAR-2.3 · RN-01, RN-05, RN-06_

- [ ] **2.2 Dominio: `LineaCarrito`, `Carrito` y excepciones**
  - `CantidadInvalidaException`, `StockInsuficienteException`, `ProductoAgotadoException` (extienden `ReglaNegocioException`).
  - `Carrito` con `agregar`, `cambiarCantidad`, `quitar`, `reconstruir`, `totales`, `totalUnidades`.
  - `CarritoTest`.
  - _Requisitos: CAR-1.2 a CAR-1.5, CAR-3.2, CAR-4.1 · RN-02, RN-03, RN-04_

- [ ] **2.3 Aplicación: DTO y `CarritoService`**
  - `ItemCarritoDTO`, `LineaResumenDTO`, `ResumenCarritoDTO`.
  - `CarritoService` sin estado, con la reconstrucción descrita en `design.md`.
  - `CarritoServiceTest` con `MemoriaProductoDAO`.
  - _Requisitos: CAR-1, CAR-2.6, CAR-3, CAR-4, CAR-5_

- [ ] **2.4 Presentación: `CarritoSesion` y `CarritoController`**
  - Rutas de `design.md`, PRG con flash. Contador de cabecera.
  - `CarritoControllerTest`.
  - _Requisitos: CAR-1.1, CAR-1.7, CAR-2.5, CAR-3.1, CAR-4_

- [ ] **2.5 Presentación: plantilla `carrito/ver.html`** y botones "Agregar" del catálogo/detalle apuntando a `POST /carrito/items`.
  - _Requisitos: CAR-2.1 a CAR-2.4, CAR-5.1_

- [ ] **2.6 Checkpoint de la spec 02**
  - A mano: agregar el monitor (S/ 549.00) y ver descuento 27.45, IGV 93.88, total 615.43. Intentar 2 mochilas (stock 1) y ver el mensaje. Intentar agregar el parlante agotado.
  - Repetir con el perfil `memoria`. `./mvnw test` en verde.
