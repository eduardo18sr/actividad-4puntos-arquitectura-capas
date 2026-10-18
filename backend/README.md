# backend · Spring Boot (Presentación → Negocio → Datos)

Proyecto Maven. Paquete base `pe.edu.ulima.tiendacapas`; un paquete por capa:

| Paquete | Capa | Clases |
|---|---|---|
| `presentacion` | Presentación (controlador REST) | `VentaController`, `VentaRequest` |
| `negocio` | Negocio (reglas RN-01..RN-03) | `VentaService`, `VentaException` |
| `datos` | Datos (SQL con `JdbcTemplate`) | `ProductoRepository`, `VentaRepository` |
| `modelo` | Compartido entre capas | `Producto`, `Venta` |

Al generar el proyecto en start.spring.io (tarea T1), usar **Name `TiendaCapas`**, copiar aquí el **contenido** de la carpeta `tiendacapas/` del zip (reemplazando los archivos vacíos) y ajustar el `pom.xml` (driver PostgreSQL, H2 solo en pruebas, `<resources>` que empaqueta `../frontend` y `<testResources>` con los scripts de `../base-de-datos`). Los pasos exactos están en `specs/steering/tecnologia.md`.

Se ejecuta desde esta carpeta, con la base de datos ya levantada (`docker compose up -d` en `../base-de-datos`):

```bash
./mvnw spring-boot:run
```

La conexión se configura con las variables de entorno `DB_URL`, `DB_USUARIO` y `DB_CLAVE` (por defecto apuntan al contenedor local). Las pruebas (`./mvnw test`) usan H2 en memoria y no necesitan Docker. Después de un `git pull`, usar `./mvnw clean test` / `./mvnw clean spring-boot:run` para no arrastrar archivos viejos de `target/`.
