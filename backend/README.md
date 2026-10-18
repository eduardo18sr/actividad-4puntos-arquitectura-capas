# backend · Spring Boot (Presentación → Negocio → Datos)

Proyecto Maven. Paquete base `pe.edu.ulima.tiendacapas`; un paquete por capa:

| Paquete | Capa | Clases |
|---|---|---|
| `presentacion` | Presentación (controlador REST) | `VentaController`, `VentaRequest` |
| `negocio` | Negocio (reglas RN-01..RN-03) | `VentaService`, `VentaException` |
| `datos` | Datos (SQL con `JdbcTemplate`) | `ProductoRepository`, `VentaRepository` |
| `modelo` | Compartido entre capas | `Producto`, `Venta` |

Al generar el proyecto en start.spring.io (tarea T1), usar **Name `TiendaCapas`**, copiar aquí el **contenido** de la carpeta `tiendacapas/` del zip (reemplazando los archivos vacíos) y agregar al `pom.xml` el bloque `<resources>` que empaqueta `../frontend` y `../base-de-datos`. Los pasos exactos están en `specs/steering/tecnologia.md`.

Se ejecuta desde esta carpeta:

```bash
./mvnw spring-boot:run
```
