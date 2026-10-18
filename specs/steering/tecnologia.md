# Steering · Tecnología

## Stack

| Pieza | Elección | Capa |
|---|---|---|
| Frontend | **HTML + CSS + JavaScript** (`fetch`), en `frontend/`. En contenedores lo sirve **nginx** (que reenvía `/api/` al backend); en desarrollo lo sirve el propio Spring Boot | Presentación (cliente) |
| Backend | **Java 21 + Spring Boot 4** (Spring Web) | Presentación (controlador), Negocio, Datos |
| Acceso a datos | **Spring JDBC** (`JdbcTemplate`) con SQL escrito a mano | Datos |
| Base de datos | **PostgreSQL 17** en su contenedor; scripts `schema.sql` y `data.sql` | Base de datos |
| Contenedores | **Docker**: una imagen por pieza (`frontend/Dockerfile`, `backend/Dockerfile`, `base-de-datos/Dockerfile`) y `docker-compose.yml` en la raíz | Despliegue |
| Pruebas | JUnit 5 (incluido en los starters de test) sobre **H2 en memoria en modo PostgreSQL**, para no depender de Docker | — |
| Build | Maven con wrapper (`mvnw`) | — |

Requisitos en la laptop: **Docker Desktop** (con `docker compose`). Para desarrollar el backend fuera de Docker, además **JDK 21 o superior**.

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
- `consultas.sql`, los `Dockerfile` y los `README.md` **no** se empaquetan.
- En la imagen Docker del backend `../frontend` no se copia: ahí el backend es **solo API** y la página la sirve el contenedor del frontend.
- Si se cambia algo en `frontend/`, hay que **reiniciar la app** para que Maven vuelva a copiarlo.

## Contenedores (`docker-compose.yml` en la raíz)

| Servicio | Imagen | Se construye con | Detalle |
|---|---|---|---|
| `frontend` | `tiendacapas-frontend:1.0` | `frontend/Dockerfile` (`nginx:1.29-alpine`) | Copia `index.html`, `app.js`, `estilos.css` y `nginx.conf`. Publica el puerto **8080** de la PC (`FRONT_PUERTO`). `nginx.conf` reenvía `/api/` a `http://backend:8080`. |
| `backend` | `tiendacapas-backend:1.0` | `backend/Dockerfile`, contexto = raíz del repo | Dos etapas: `maven:3.9-eclipse-temurin-21` compila, **corre las pruebas** y empaqueta; `eclipse-temurin:21-jre-alpine` ejecuta el `.jar` con un usuario sin privilegios. `DB_URL=jdbc:postgresql://db:5432/tienda`, `TZ=America/Lima`. **No** publica puertos: solo el frontend le habla. Espera a que la BD esté `healthy`. |
| `db` | `tiendacapas-db:1.0` | `base-de-datos/Dockerfile` (`postgres:17-alpine`) | Lleva dentro `schema.sql` (`01-schema.sql`) y `data.sql` (`02-data.sql`): al crearse por **primera vez** (volumen vacío) crea las tablas y la semilla. Base/usuario/clave `tienda` (credenciales de demo). `HEALTHCHECK` con `pg_isready`. Puerto **5432** de la PC (`DB_PUERTO`) solo para consultarla o para el modo desarrollo. |

- Los datos se guardan en el volumen `datos-tienda`: **sobreviven** a `docker compose stop`/`down` y a reconstruir imágenes. `docker compose down -v` los borra.
- Los contenedores se hablan por la red interna de compose usando el nombre del servicio (`backend`, `db`).
- Cada `Dockerfile` tiene su `.dockerignore` (`backend/Dockerfile.dockerignore` para el backend) para enviar al build solo lo necesario.

## Por qué este stack (para la PPT y el video)

| Decisión | Alternativa descartada | Razón |
|---|---|---|
| **Spring Boot** | Node/Express, Django | El grupo trabaja en Java. Spring inyecta cada capa en la de arriba por constructor, así las dependencias entre capas quedan explícitas. |
| **HTML + JS simple** | React/Angular | Para una sola pantalla un framework de frontend es complejidad innecesaria: son archivos estáticos que cualquier servidor web entrega. |
| **nginx para el frontend** | Servir la página desde Spring Boot | Cada pieza tiene su imagen. nginx entrega los estáticos y reenvía `/api/` al backend, así `app.js` usa rutas relativas (`/api/...`) y el navegador nunca conoce la dirección del backend ni de la BD. |
| **JdbcTemplate con SQL a mano** | Spring Data JPA | El SQL queda visible dentro de la capa de datos: en el video se ve exactamente qué hace esa capa. |
| **PostgreSQL** | H2 en memoria, MySQL | Base de datos real y libre: los datos persisten y se consultan con `psql`. El SQL usado es estándar (`GENERATED ALWAYS AS IDENTITY`, `NUMERIC`), por eso las pruebas pueden correr sobre H2 en modo PostgreSQL con los **mismos** scripts. |
| **Docker Compose** | Instalar Java, PostgreSQL y un servidor web en cada laptop | Un solo comando (`docker compose up -d --build`) levanta las tres piezas con las mismas versiones, tablas y datos semilla en cualquier máquina. Desplegar en otro servidor solo requiere Docker. |
| **H2 solo en pruebas** | Testcontainers | `./mvnw test` corre en segundos y sin Docker. La concurrencia real (`UPDATE … AND stock >= ?`) se verificó además contra PostgreSQL (tarea T10). |

## Comandos

Todo en contenedores (desde la raíz):

```bash
docker compose up -d --build
```

- App: `http://localhost:8080`
- Recorrido por capas: `docker compose logs -f backend`
- Consola de la BD: `docker exec -it tiendacapas-db psql -U tienda -d tienda` (consultas en `base-de-datos/consultas.sql`)
- Volver a los datos semilla: `docker compose down -v` y `docker compose up -d`
- Detener conservando los datos: `docker compose stop`

Desarrollo (solo la BD en Docker, backend con Maven):

```bash
docker compose up -d db
```

```bash
cd backend && ./mvnw clean spring-boot:run
```

- Pruebas (no necesitan Docker): `./mvnw clean test` desde `backend/`
- En Windows (PowerShell) usar `.\mvnw.cmd` en lugar de `./mvnw`.
- Después de un `git pull`, compilar con `clean` para no arrastrar archivos viejos de `target/`.

### Si el puerto 5432 u 8080 ya está ocupado

Pasa cuando la PC ya tiene un PostgreSQL instalado (5432) u otra app en el 8080. Se eligen otros puertos con variables, desde la raíz del repo.

Windows (PowerShell):

```powershell
$env:DB_PUERTO="5433"; $env:FRONT_PUERTO="8081"; docker compose up -d --build
```

Linux / macOS / Git Bash:

```bash
DB_PUERTO=5433 FRONT_PUERTO=8081 docker compose up -d --build
```

Para no escribirlo cada vez, crear un archivo `.env` en la raíz con `DB_PUERTO=5433` y/o `FRONT_PUERTO=8081` (lo lee `docker compose`; no se sube al repo).

En modo desarrollo (backend con `mvnw`), si la BD quedó en el 5433 hay que avisarle al backend:

```powershell
cd backend
$env:DB_URL="jdbc:postgresql://localhost:5433/tienda"; .\mvnw.cmd spring-boot:run
```

## `backend/src/main/resources/application.properties`

```properties
spring.application.name=tiendacapas
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/tienda}
spring.datasource.username=${DB_USUARIO:tienda}
spring.datasource.password=${DB_CLAVE:tienda}
spring.sql.init.mode=never
```

- Los valores por defecto apuntan a la BD publicada en `localhost:5432` (modo desarrollo). En la imagen Docker, `DB_URL` vale `jdbc:postgresql://db:5432/tienda`. Para desplegar contra otra BD PostgreSQL se definen las variables de entorno `DB_URL`, `DB_USUARIO` y `DB_CLAVE`, sin recompilar.
- `spring.sql.init.mode=never`: el backend **no** crea tablas; eso es responsabilidad de la base de datos (su contenedor).

## `backend/src/test/resources/application.properties` (solo pruebas)

```properties
spring.datasource.url=jdbc:h2:mem:tienda;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;DB_CLOSE_DELAY=-1
spring.datasource.username=sa
spring.datasource.password=
spring.sql.init.mode=always
```

Las pruebas crean una BD H2 en memoria con `schema.sql` y `data.sql` en cada ejecución.
