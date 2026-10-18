# TiendaCapas · Arquitectura en Capas (Grupo 5)

Actividad grupal de Ingeniería de Software II (2026-2) · **Parte C: implementación y demostración en vivo**.
Estilo asignado: **Arquitectura en Capas**. Caso acotado: **registrar una venta**. Tecnología: **Java 21 + Spring Boot + PostgreSQL**, cada pieza en su **contenedor Docker**.

El enunciado completo está en `ISW2_Actividad_Grupal_Arquitecturas_Software (1).md`.

## Idea en una línea

Una sola funcionalidad —**registrar una venta**— que atraviesa todas las capas: la página web (frontend) envía la venta, el backend la valida y calcula el total, y la base de datos la guarda. **No se busca complejidad**: se busca que las capas se vean y se puedan explicar.

```
Navegador: index.html + app.js                       ← FRONTEND (contenedor nginx)
        │  HTTP / JSON (fetch) → nginx reenvía /api/
        ▼
 PRESENTACIÓN   VentaController      recibe y responde  ┐
        │                                                │
        ▼                                                │ BACKEND
 NEGOCIO        VentaService         reglas y cálculo    │ (contenedor Spring Boot)
        │                                                │
        ▼                                                │
 DATOS          ProductoRepository   SQL (JdbcTemplate)  │
                VentaRepository                          ┘
        │  JDBC
        ▼
 BASE DE DATOS  PostgreSQL (contenedor): tablas producto y venta
```

Cada capa **solo llama a la de abajo**. El frontend nunca toca la BD; el controlador nunca escribe SQL; el repositorio nunca decide reglas de negocio.

## Cómo está organizado (SDD)

Primero se especifica, luego se codea. Una sola spec.

| Ruta | Contenido |
|---|---|
| `AGENTS.md` / `CLAUDE.md` | Reglas para quien implemente (persona o agente de IA) |
| `docker-compose.yml` | Levanta los tres contenedores: frontend, backend y base de datos |
| `frontend/` | Capa de presentación (cliente): `index.html`, `app.js`, `estilos.css`; `Dockerfile` + `nginx.conf` |
| `backend/` | Spring Boot: paquetes `presentacion`, `negocio`, `datos`, `modelo`; `Dockerfile` |
| `base-de-datos/` | PostgreSQL: `Dockerfile`, `schema.sql`, `data.sql`, `consultas.sql` |
| `specs/steering/producto.md` | Qué hace el sistema, alcance y reglas de negocio RN-01 a RN-03 |
| `specs/steering/tecnologia.md` | Stack, justificación y comandos |
| `specs/steering/estructura.md` | Las capas, reglas de dependencia y árbol de archivos |
| `specs/arquitectura/arquitectura.md` | Diagramas (capas y secuencia) y **por qué es Arquitectura en Capas** |
| `specs/01-registrar-venta/` | `requirements.md` → `design.md` → `tasks.md` de la única funcionalidad |
| `specs/demo/guion-video2.md` | Guion del Video 2 |

## Cómo ejecutarlo

Requisitos: **Docker Desktop**. Para el modo desarrollo, además **JDK 21+**.

### Opción A · Todo en contenedores (recomendado para la demo)

Desde la raíz del repo:

```bash
docker compose up -d --build
```

Abrir `http://localhost:8080`. Se levantan tres contenedores, uno por pieza:

| Contenedor | Imagen | Qué tiene | Puerto en la PC |
|---|---|---|---|
| `tiendacapas-frontend` | `tiendacapas-frontend:1.0` (nginx) | `index.html`, `app.js`, `estilos.css`; reenvía `/api/` al backend | 8080 |
| `tiendacapas-backend` | `tiendacapas-backend:1.0` (Java 21) | Capas presentación (API), negocio y datos | ninguno (solo red interna) |
| `tiendacapas-db` | `tiendacapas-db:1.0` (PostgreSQL 17) | Tablas y datos semilla | 5432 |

- Ver el recorrido por capas: `docker compose logs -f backend`
- Ver la BD: `docker exec -it tiendacapas-db psql -U tienda -d tienda` (consultas en `base-de-datos/consultas.sql`)
- Volver a los datos semilla: `docker compose down -v` y `docker compose up -d`
- Detener conservando los datos: `docker compose stop`
- Tras cambiar código: `docker compose up -d --build` reconstruye las imágenes (la del backend corre las pruebas al construirse)

### Opción B · Desarrollo (backend con Maven)

```bash
docker compose up -d db
```

Luego, desde `backend/`:

```bash
./mvnw clean spring-boot:run
```

Abrir `http://localhost:8080` (aquí el propio backend sirve el frontend). Si los contenedores `frontend` y `backend` de la opción A están corriendo, detenerlos antes (`docker compose stop frontend backend`) porque usan el mismo puerto.

- Pruebas (no necesitan Docker): `./mvnw clean test` desde `backend/`.
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
