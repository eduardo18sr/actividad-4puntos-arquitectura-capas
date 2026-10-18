# backend · Spring Boot (Presentación → Negocio → Datos)

Proyecto Maven. Paquete base `pe.edu.ulima.tiendacapas`; un paquete por capa:

| Paquete | Capa | Clases |
|---|---|---|
| `presentacion` | Presentación (controlador REST) | `VentaController`, `VentaRequest` |
| `negocio` | Negocio (reglas RN-01..RN-03) | `VentaService`, `VentaException` |
| `datos` | Datos (SQL con `JdbcTemplate`) | `ProductoRepository`, `VentaRepository` |
| `modelo` | Compartido entre capas | `Producto`, `Venta` |

Al generar el proyecto en start.spring.io (tarea T1), descomprimirlo **dentro de esta carpeta** conservando los archivos ya creados (`pom.xml`, `mvnw` y `.mvn/` vienen del generador).

Se ejecuta desde esta carpeta:

```bash
./mvnw spring-boot:run
```
