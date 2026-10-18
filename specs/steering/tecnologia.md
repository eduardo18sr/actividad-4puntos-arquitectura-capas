# Steering · Tecnología

## Stack elegido

| Pieza | Elección | Uso en la arquitectura |
|---|---|---|
| Lenguaje | **Java 21 (LTS)** — Java 25 LTS también sirve | Todo el sistema |
| Framework | **Spring Boot** (versión estable que ofrezca start.spring.io al crear el proyecto) | Contenedor de inyección de dependencias que **cablea las capas** sin que se conozcan por clases concretas |
| Presentación | **Spring MVC + Thymeleaf** (HTML renderizado en servidor) + CSS simple | Capa de presentación |
| Validación de formularios | **Bean Validation** (`spring-boot-starter-validation`) | Validación de formato en presentación; las reglas reales se revalidan en el dominio |
| Persistencia | **Spring JDBC** (`JdbcClient` o `JdbcTemplate`) con SQL escrito a mano | Implementación de los DAO en la capa de persistencia |
| Base de datos | **H2 en memoria** (`jdbc:h2:mem:tiendacapas`) con `schema.sql` y `data.sql` | Fuente de datos; se recrea en cada arranque → demo reproducible |
| Transversal | **Spring AOP** (starter "AOP"/"AspectJ" según la versión de Boot) | Aspecto que imprime la traza de cada capa en consola |
| Pruebas | **JUnit 5**, **AssertJ**, **Spring Boot Test**, **ArchUnit** (`com.tngtech.archunit:archunit-junit5`, última 1.x) | Pruebas de dominio sin BD, de servicios con DAO en memoria, de DAO con H2 y **reglas de capas** |
| Build | **Maven** (wrapper `mvnw` incluido) | Un solo artefacto: `tiendacapas.jar` |

### Dependencias al generar en start.spring.io

- Project: Maven · Language: Java · Java: 21 · Packaging: Jar
- Group: `pe.edu.ulima` · Artifact: `tiendacapas` · Package: `pe.edu.ulima.tiendacapas`
- Dependencies: **Spring Web**, **Thymeleaf**, **Validation**, **JDBC API**, **H2 Database**, **Spring Boot DevTools**, **AOP** (si aparece como "Spring AOP" o "AspectJ").
- Agregar a mano en `pom.xml` (scope `test`): `com.tngtech.archunit:archunit-junit5`.

> Si la versión de Spring Boot es 4.x, algunos artefactos cambian de nombre (por ejemplo, la consola de H2 o el starter de AOP pueden venir como módulos aparte). El diseño **no cambia**; solo se ajusta el `pom.xml`.

## Por qué este stack (justificación para la PPT y el video)

| Decisión | Alternativa descartada | Razón |
|---|---|---|
| **Java + Spring Boot** | Node/Express, Python/Django | Requisito del grupo (Java). Spring hace la **inyección de dependencias**: el servicio de aplicación recibe una interfaz DAO y Spring decide qué implementación inyectar. Eso es lo que permite cambiar la persistencia sin tocar otras capas. |
| **Thymeleaf (renderizado en servidor)** | SPA en React/Angular | Con una SPA el sistema pasa a tener dos desplegables (frontend y API) y la demo se desvía hacia **multi-tier**. Con Thymeleaf hay **un solo desplegable**, y la separación que se demuestra es **lógica (capas)**, que es el tema asignado. |
| **Spring JDBC con SQL a mano** | Spring Data JPA | Con JPA las entidades del dominio se llenan de anotaciones `@Entity`/`@Column` (la persistencia se filtra al dominio) y el DAO "desaparece" detrás de una interfaz mágica. Con JDBC el **dominio queda puro** y el DAO muestra su SQL: se ve claramente qué hace cada capa. |
| **H2 en memoria** | PostgreSQL/MySQL | Cero instalación, la demo arranca igual en cualquier laptop y los datos semilla se recrean en cada arranque. Cambiar a PostgreSQL solo afecta configuración y persistencia (argumento a favor de las capas). |
| **ArchUnit** | Revisión manual de imports | Convierte la regla "presentación no toca persistencia" en una **prueba automática** que falla el build. Es la evidencia más fuerte de que el estilo se aplica de verdad. |
| **Spring AOP para la traza** | `System.out.println` en cada clase | La traza es un **servicio transversal**: un solo aspecto la aplica a todas las capas sin ensuciar su código. Ilustra la "capa de infraestructura transversal". |

## Comandos que deben funcionar

```bash
./mvnw spring-boot:run
```
App en `http://localhost:8080`. Perfil por defecto: persistencia **JDBC + H2**.

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=memoria
```
Misma app con persistencia **en memoria** (colecciones Java). Ninguna otra capa cambia.

```bash
./mvnw test
```
Todas las pruebas, incluidas las reglas de arquitectura.

```bash
./mvnw test -Dtest=ArquitecturaCapasTest
```
Solo las reglas de capas (se usa en la demo).

Consola H2 (solo perfil por defecto): `http://localhost:8080/h2-console`, JDBC URL `jdbc:h2:mem:tiendacapas`, usuario `sa`, sin contraseña.

En Windows (PowerShell) usar `.\mvnw.cmd` en lugar de `./mvnw`.

## Configuración (`application.properties`)

```properties
spring.application.name=tiendacapas
spring.datasource.url=jdbc:h2:mem:tiendacapas;DB_CLOSE_DELAY=-1
spring.datasource.username=sa
spring.datasource.password=
spring.sql.init.mode=always
spring.h2.console.enabled=true
spring.thymeleaf.cache=false
logging.level.pe.edu.ulima.tiendacapas.transversal=INFO
```

En `application-memoria.properties`: desactivar la inicialización SQL y la consola H2 (`spring.sql.init.mode=never`, `spring.h2.console.enabled=false`), y excluir el `DataSource` autoconfigurado si hiciera falta.
