# Spec 00 · Fundaciones de las capas — Tareas

> Ejecutar en orden. Al terminar cada tarea: `./mvnw test` en verde y marcar la casilla.

- [ ] **0.1 Generar el proyecto**
  - Crear con start.spring.io según `specs/steering/tecnologia.md` (Maven, Java 21, paquete `pe.edu.ulima.tiendacapas`). Agregar `archunit-junit5` en scope `test`.
  - Copiar el `application.properties` de `tecnologia.md`; crear `application-memoria.properties`.
  - **Resultado**: `./mvnw spring-boot:run` arranca en el puerto 8080.
  - _Requisitos: BASE-1.1, BASE-1.3_

- [ ] **0.2 Crear los cinco paquetes de capa**
  - `presentacion`, `aplicacion`, `dominio`, `persistencia`, `transversal`, con los subpaquetes de `estructura.md` (un `package-info.java` por capa con un Javadoc que describa su responsabilidad).
  - **Resultado**: el árbol coincide con `estructura.md`.
  - _Requisitos: BASE-2_

- [ ] **0.3 Escribir `ArquitecturaCapasTest`** (antes de escribir lógica)
  - Reglas AR-01 a AR-07 tal como en `design.md`, cada una con `.because(...)` en español.
  - Verificar a mano que falla: crear temporalmente en `presentacion` una clase que importe algo de `persistencia`, correr la prueba, ver el fallo y borrar la clase.
  - **Resultado**: prueba en verde con el esqueleto; en rojo con la violación.
  - _Requisitos: BASE-3.1 a BASE-3.4_

- [ ] **0.4 Transversal: `ReglaNegocioException`**
  - Clase abstracta en `transversal.error`, Java puro.
  - _Requisitos: BASE-6.1_

- [ ] **0.5 Dominio: `Producto` mínimo y contratos DAO**
  - `Producto` con sus campos y constructor (las reglas se agregan en la spec 01).
  - `ProductoDAO` y `VentaDAO` con las firmas de `design.md`; `Venta` como clase marcador si aún no existe.
  - _Requisitos: BASE-2.2, BASE-4.4_

- [ ] **0.6 Esquema y semilla SQL**
  - `schema.sql` y `data.sql` con las 8 filas de `design.md`.
  - **Resultado**: en `/h2-console`, `SELECT * FROM producto` devuelve 8 filas.
  - _Requisitos: BASE-7.1, BASE-7.3_

- [ ] **0.7 Persistencia JDBC: `JdbcProductoDAO` (lectura) y esqueleto de `JdbcVentaDAO`**
  - `@Repository @Profile("!memoria")`, inyección de `JdbcClient` por constructor.
  - Implementar `listar`, `buscarPorId`, `buscarPorIds`, `listarCategorias` con un `RowMapper` → `Producto`. `descontarStock` y `JdbcVentaDAO` pueden lanzar `UnsupportedOperationException` hasta la spec 03.
  - Prueba `JdbcProductoDAOSemillaTest`.
  - _Requisitos: BASE-4.1, BASE-7.1_

- [ ] **0.8 Persistencia en memoria: `MemoriaProductoDAO`, `MemoriaVentaDAO`, `DatosSemillaMemoria`**
  - `@Repository @Profile("memoria")`, `ConcurrentHashMap` interno, misma semilla.
  - Mismo comportamiento de filtros y orden que la versión JDBC.
  - Prueba `PerfilMemoriaTest`.
  - _Requisitos: BASE-4.2, BASE-7.2_

- [ ] **0.9 Transversal: `TrazaCapasAspect`**
  - Según `design.md` (pointcut, sangría por `ThreadLocal`, duración, excepción).
  - **Resultado**: al abrir `/productos` (cuando exista la spec 01) aparecen líneas `→ [PRESENTACIÓN] …` y `→ [APLICACIÓN] …` anidadas.
  - _Requisitos: BASE-5.1 a BASE-5.4_

- [ ] **0.10 Presentación: layout, `InicioController`, `ManejadorErroresWeb` y `error.html`**
  - Layout con cabecera (Catálogo · Carrito (n) · Ventas) y zona de alertas flash (`exito`, `error`).
  - Prueba `ManejadorErroresWebTest`.
  - _Requisitos: BASE-1.2, BASE-6.2 a BASE-6.5_

- [ ] **0.11 Checkpoint de la spec 00**
  - `./mvnw test` en verde. App arranca con y sin `-Dspring-boot.run.profiles=memoria`.
  - Actualizar `arquitectura.md` si algún nombre cambió durante la implementación.
