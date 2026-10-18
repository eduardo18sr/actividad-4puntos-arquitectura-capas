# Steering · Tecnología

## Stack

| Pieza | Elección | Capa |
|---|---|---|
| Frontend | **HTML + CSS + JavaScript** (`fetch`), servido por Spring Boot desde `static/` | Presentación (cliente) |
| Backend | **Java 21 + Spring Boot** (Spring Web) | Presentación (controlador), Negocio, Datos |
| Acceso a datos | **Spring JDBC** (`JdbcTemplate`) con SQL escrito a mano | Datos |
| Base de datos | **H2 en memoria** con `schema.sql` y `data.sql` | Base de datos |
| Pruebas | JUnit 5 (incluido en `spring-boot-starter-test`) | — |
| Build | Maven con wrapper (`mvnw`) | — |

### Generar el proyecto en start.spring.io

- Project: Maven · Language: Java · Java: 21 · Packaging: Jar
- Group: `pe.edu.ulima` · Artifact: `tiendacapas` · Package: `pe.edu.ulima.tiendacapas`
- Dependencies: **Spring Web**, **JDBC API**, **H2 Database**.

## Por qué este stack (para la PPT y el video)

| Decisión | Alternativa descartada | Razón |
|---|---|---|
| **Spring Boot** | Node/Express, Django | El grupo trabaja en Java. Spring inyecta cada capa en la de arriba por constructor, así las dependencias entre capas quedan explícitas. |
| **HTML + JS simple** | React/Angular | Para una sola pantalla un framework de frontend es complejidad innecesaria. Se sirve desde el mismo backend: **un solo desplegable**, así lo que se demuestra es la separación **lógica** (capas) y no la física (multi-tier). |
| **JdbcTemplate con SQL a mano** | Spring Data JPA | El SQL queda visible dentro de la capa de datos: en el video se ve exactamente qué hace esa capa. |
| **H2 en memoria** | PostgreSQL/MySQL | Cero instalación; la demo arranca igual en cualquier laptop y los datos semilla se recrean en cada arranque. Cambiar a otra BD solo afectaría configuración y capa de datos. |

## Comandos

```bash
./mvnw spring-boot:run
```

- App: `http://localhost:8080`
- Consola H2: `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:tienda`, usuario `sa`, sin contraseña)
- En Windows (PowerShell): `.\mvnw.cmd spring-boot:run`

## `application.properties`

```properties
spring.application.name=tiendacapas
spring.datasource.url=jdbc:h2:mem:tienda;DB_CLOSE_DELAY=-1
spring.datasource.username=sa
spring.datasource.password=
spring.sql.init.mode=always
spring.h2.console.enabled=true
```

> Si la versión de Spring Boot no habilita la consola H2 solo con esa propiedad, agregar la dependencia que indique la documentación de esa versión. El diseño no cambia.
