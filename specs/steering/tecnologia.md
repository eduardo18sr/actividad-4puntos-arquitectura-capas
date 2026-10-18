# Steering · Tecnología

## Stack

| Pieza | Elección | Capa |
|---|---|---|
| Frontend | **HTML + CSS + JavaScript** (`fetch`), en la carpeta `frontend/`, servido por el mismo Spring Boot | Presentación (cliente) |
| Backend | **Java 21 + Spring Boot** (Spring Web) | Presentación (controlador), Negocio, Datos |
| Acceso a datos | **Spring JDBC** (`JdbcTemplate`) con SQL escrito a mano | Datos |
| Base de datos | **H2 en memoria**; scripts en `base-de-datos/` (`schema.sql`, `data.sql`) | Base de datos |
| Pruebas | JUnit 5 (incluido en `spring-boot-starter-test`) | — |
| Build | Maven con wrapper (`mvnw`) | — |

### Generar el proyecto en start.spring.io

- Project: Maven · Language: Java · Java: 21 · Packaging: Jar
- Group: `pe.edu.ulima` · Artifact: `tiendacapas` · **Name: `TiendaCapas`** · Package: `pe.edu.ulima.tiendacapas`
- Dependencies: **Spring Web**, **JDBC API**, **H2 Database**.

> **Name tiene que ser `TiendaCapas`.** Si se deja el valor por defecto, el generador crea la clase `TiendacapasApplication` (con «c» minúscula). En Windows ese archivo choca con el `TiendaCapasApplication.java` que ya existe en el repo (el sistema no distingue mayúsculas) y el proyecto no compila.

#### Colocar el proyecto generado en `backend/`

1. El zip trae una carpeta `tiendacapas/`. Copiar **su contenido** (no la carpeta) dentro de `backend/`.
2. Aceptar que reemplace los archivos vacíos que ya existen (`TiendaCapasApplication.java`, `application.properties`).
3. Borrar `src/test/.../TiendaCapasApplicationTests.java` y `HELP.md` (no están en el diseño). Conservar `.gitignore`, `mvnw`, `mvnw.cmd`, `.mvn/` y `pom.xml`.
4. Agregar al `pom.xml` el bloque `<resources>` de la sección siguiente.

#### `pom.xml`: empaquetar `frontend/` y `base-de-datos/` dentro del backend

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
    <resource>
        <directory>../base-de-datos</directory>
        <includes>
            <include>schema.sql</include>
            <include>data.sql</include>
        </includes>
    </resource>
</resources>
```

Al compilar, Maven copia `frontend/` a `static/` y `schema.sql` y `data.sql` a la raíz del classpath. Spring Boot los encuentra ahí por defecto: sirve la página en `/` y ejecuta los scripts al arrancar. Así:

- La app funciona igual si se arranca desde la terminal, desde el IDE o con `java -jar`, sin importar la carpeta de trabajo.
- El `.jar` es **un solo desplegable** que ya lleva dentro el frontend y los scripts.
- `consultas.sql` y los `README.md` **no** se empaquetan (son solo para el equipo).
- Si se cambia algo en `frontend/` o `base-de-datos/`, hay que **reiniciar la app** para que Maven vuelva a copiarlo.

## Por qué este stack (para la PPT y el video)

| Decisión | Alternativa descartada | Razón |
|---|---|---|
| **Spring Boot** | Node/Express, Django | El grupo trabaja en Java. Spring inyecta cada capa en la de arriba por constructor, así las dependencias entre capas quedan explícitas. |
| **HTML + JS simple** | React/Angular | Para una sola pantalla un framework de frontend es complejidad innecesaria. Se sirve desde el mismo backend: **un solo desplegable**, así lo que se demuestra es la separación **lógica** (capas) y no la física (multi-tier). |
| **JdbcTemplate con SQL a mano** | Spring Data JPA | El SQL queda visible dentro de la capa de datos: en el video se ve exactamente qué hace esa capa. |
| **H2 en memoria** | PostgreSQL/MySQL | Cero instalación; la demo arranca igual en cualquier laptop y los datos semilla se recrean en cada arranque. Cambiar a otra BD solo afectaría configuración y capa de datos. |

## Comandos

Desde la carpeta `backend/`:

```bash
./mvnw spring-boot:run
```

- App: `http://localhost:8080`
- Consola H2: `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:tienda`, usuario `sa`, sin contraseña)
- En Windows (PowerShell): `cd backend` y luego `.\mvnw.cmd spring-boot:run`

## `backend/src/main/resources/application.properties`

```properties
spring.application.name=tiendacapas
spring.datasource.url=jdbc:h2:mem:tienda;DB_CLOSE_DELAY=-1
spring.datasource.username=sa
spring.datasource.password=
spring.sql.init.mode=always
spring.h2.console.enabled=true
```

No hacen falta rutas a `../frontend` ni a `../base-de-datos`: el `pom.xml` ya los pone en el classpath (ver arriba).

> Si la versión de Spring Boot no habilita la consola H2 solo con esa propiedad, agregar la dependencia que indique la documentación de esa versión. El diseño no cambia.
