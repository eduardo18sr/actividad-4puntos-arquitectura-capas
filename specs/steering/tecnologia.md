# Steering · Tecnología

## Stack

| Pieza | Elección | Capa |
|---|---|---|
| Frontend | **HTML + CSS + JavaScript** (`fetch`), en la carpeta `frontend/`, servido por el mismo Spring Boot | Presentación (cliente) |
| Backend | **Java 21 + Spring Boot 4** (Spring Web) | Presentación (controlador), Negocio, Datos |
| Acceso a datos | **Spring JDBC** (`JdbcTemplate`) con SQL escrito a mano | Datos |
| Base de datos | **PostgreSQL 17 en un contenedor Docker** (`base-de-datos/docker-compose.yml`); scripts `schema.sql` y `data.sql` | Base de datos |
| Pruebas | JUnit 5 (incluido en los starters de test) sobre **H2 en memoria en modo PostgreSQL**, para no depender de Docker | — |
| Build | Maven con wrapper (`mvnw`) | — |

Requisitos en la laptop: **JDK 21 o superior** y **Docker Desktop** (con `docker compose`).

### Generar el proyecto en start.spring.io

- Project: Maven · Language: Java · Java: 21 · Packaging: Jar
- Group: `pe.edu.ulima` · Artifact: `tiendacapas` · **Name: `TiendaCapas`** · Package: `pe.edu.ulima.tiendacapas`
- Dependencies: **Spring Web**, **JDBC API**, **H2 Database** (luego se ajustan en el `pom.xml`, ver abajo).

> **Name tiene que ser `TiendaCapas`.** Si se deja el valor por defecto, el generador crea la clase `TiendacapasApplication` (con «c» minúscula). En Windows ese archivo choca con el `TiendaCapasApplication.java` que ya existe en el repo (el sistema no distingue mayúsculas) y el proyecto no compila.

#### Colocar el proyecto generado en `backend/`

1. El zip trae una carpeta `tiendacapas/`. Copiar **su contenido** (no la carpeta) dentro de `backend/`.
2. Aceptar que reemplace los archivos vacíos que ya existen (`TiendaCapasApplication.java`, `application.properties`).
3. Borrar `src/test/.../TiendaCapasApplicationTests.java` y `HELP.md` (no están en el diseño). Conservar `.gitignore`, `mvnw`, `mvnw.cmd`, `.mvn/` y `pom.xml`.
4. Ajustar el `pom.xml` como en la sección siguiente.

#### `pom.xml`

Dependencias de base de datos (se quita `spring-boot-h2console`):

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

Dentro de `<build>`, junto a `<plugins>`:

```xml
<resources>
    <resource>
        <directory>src/main/resources</directory>
    </resource>
    <resource>
        <directory>../frontend</directory>
        <targetPath>static</targetPath>
        <excludes>
            <exclude>README.md</exclude>
        </excludes>
    </resource>
</resources>
<testResources>
    <testResource>
        <directory>src/test/resources</directory>
    </testResource>
    <testResource>
        <directory>../base-de-datos</directory>
        <includes>
            <include>schema.sql</include>
            <include>data.sql</include>
        </includes>
    </testResource>
</testResources>
```

- Al compilar, Maven copia `frontend/` a `static/`: Spring Boot sirve la página en `/`. La app funciona igual desde la terminal, el IDE o con `java -jar`, y el `.jar` lleva dentro el frontend.
- `schema.sql` y `data.sql` **solo** se copian para las pruebas: la BD real la crea el contenedor (ver abajo).
- `consultas.sql`, `docker-compose.yml` y los `README.md` **no** se empaquetan.
- Si se cambia algo en `frontend/`, hay que **reiniciar la app** para que Maven vuelva a copiarlo.

## Base de datos en Docker (`base-de-datos/docker-compose.yml`)

- Imagen `postgres:17-alpine`, contenedor `tiendacapas-db`, puerto `5432` de la PC (configurable con la variable `DB_PUERTO`, ver "Si el puerto 5432 ya está ocupado").
- Base `tienda`, usuario `tienda`, contraseña `tienda` (credenciales de demo, no de producción).
- Al crearse por **primera vez** (volumen vacío), el contenedor ejecuta `schema.sql` y luego `data.sql` (montados como `01-schema.sql` y `02-data.sql` en `/docker-entrypoint-initdb.d`).
- Los datos se guardan en el volumen `datos-tienda`: **sobreviven** a reinicios del backend y del contenedor.
- `healthcheck` con `pg_isready`: `docker compose ps` muestra `healthy` cuando la BD ya acepta conexiones.

## Por qué este stack (para la PPT y el video)

| Decisión | Alternativa descartada | Razón |
|---|---|---|
| **Spring Boot** | Node/Express, Django | El grupo trabaja en Java. Spring inyecta cada capa en la de arriba por constructor, así las dependencias entre capas quedan explícitas. |
| **HTML + JS simple** | React/Angular | Para una sola pantalla un framework de frontend es complejidad innecesaria. Se sirve desde el mismo backend, así no hace falta otro servidor. |
| **JdbcTemplate con SQL a mano** | Spring Data JPA | El SQL queda visible dentro de la capa de datos: en el video se ve exactamente qué hace esa capa. |
| **PostgreSQL** | H2 en memoria, MySQL | Base de datos real y libre: los datos persisten y se consultan con `psql`. El SQL usado es estándar (`GENERATED ALWAYS AS IDENTITY`, `NUMERIC`), por eso las pruebas pueden correr sobre H2 en modo PostgreSQL con los **mismos** scripts. |
| **Docker Compose para la BD** | Instalar PostgreSQL en cada laptop | Un solo comando (`docker compose up -d`) levanta la misma BD, con la misma versión, tablas y datos semilla en cualquier máquina. Desplegar en otro servidor solo requiere Docker y cambiar `DB_URL`. |
| **H2 solo en pruebas** | Testcontainers | `./mvnw test` corre en segundos y sin Docker. La concurrencia real (`UPDATE … AND stock >= ?`) se verificó además contra PostgreSQL (tarea T10). |

## Comandos

1. Levantar la base de datos (desde `base-de-datos/`):

```bash
docker compose up -d
```

2. Levantar el backend (desde `backend/`):

```bash
./mvnw spring-boot:run
```

- App: `http://localhost:8080`
- Consola de la BD: `docker exec -it tiendacapas-db psql -U tienda -d tienda` (consultas en `base-de-datos/consultas.sql`)
- Volver a los datos semilla: `docker compose down -v` y luego `docker compose up -d` (desde `base-de-datos/`)
- Detener la BD conservando los datos: `docker compose stop`
- Pruebas (no necesitan Docker): `./mvnw test` desde `backend/`
- En Windows (PowerShell) usar `.\mvnw.cmd` en lugar de `./mvnw`.
- Después de un `git pull`, compilar con `clean` (`./mvnw clean test`, `./mvnw clean spring-boot:run`): así no quedan en `target/` archivos de una versión anterior (por ejemplo, un `schema.sql` viejo hace fallar las pruebas).

### Si el puerto 5432 ya está ocupado

Pasa cuando la PC ya tiene un PostgreSQL instalado: el contenedor no puede publicar el 5432 (o el backend termina conectándose al PostgreSQL local y falla el usuario `tienda`). Se usa otro puerto, por ejemplo 5433, y se le avisa al backend con `DB_URL`.

Windows (PowerShell):

```powershell
cd base-de-datos
$env:DB_PUERTO="5433"; docker compose up -d
cd ..\backend
$env:DB_URL="jdbc:postgresql://localhost:5433/tienda"; .\mvnw.cmd spring-boot:run
```

Linux / macOS / Git Bash:

```bash
cd base-de-datos && DB_PUERTO=5433 docker compose up -d
cd ../backend && DB_URL=jdbc:postgresql://localhost:5433/tienda ./mvnw spring-boot:run
```

Para no escribirlo cada vez, se puede crear `base-de-datos/.env` con la línea `DB_PUERTO=5433` (lo lee `docker compose`; no se sube al repo).

## `backend/src/main/resources/application.properties`

```properties
spring.application.name=tiendacapas
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/tienda}
spring.datasource.username=${DB_USUARIO:tienda}
spring.datasource.password=${DB_CLAVE:tienda}
spring.sql.init.mode=never
```

- Los valores por defecto coinciden con el contenedor. Para desplegar contra otra BD PostgreSQL se definen las variables de entorno `DB_URL`, `DB_USUARIO` y `DB_CLAVE`, sin recompilar.
- `spring.sql.init.mode=never`: el backend **no** crea tablas; eso es responsabilidad de la base de datos (su contenedor).

## `backend/src/test/resources/application.properties` (solo pruebas)

```properties
spring.datasource.url=jdbc:h2:mem:tienda;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;DB_CLOSE_DELAY=-1
spring.datasource.username=sa
spring.datasource.password=
spring.sql.init.mode=always
```

Las pruebas crean una BD H2 en memoria con `schema.sql` y `data.sql` en cada ejecución.
