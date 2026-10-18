# TiendaCapas · Arquitectura en Capas (Grupo 5)

Actividad grupal de Ingeniería de Software II (2026-2) · **Parte C: implementación y demostración en vivo**.
Estilo asignado: **Arquitectura en Capas**. Caso acotado: **registrar una venta**. Tecnología: **Java 21 + Spring Boot + H2**.

El enunciado completo está en `ISW2_Actividad_Grupal_Arquitecturas_Software (1).md`.

## Idea en una línea

Una sola funcionalidad —**registrar una venta**— que atraviesa todas las capas: la página web (frontend) envía la venta, el backend la valida y calcula el total, y la base de datos la guarda. **No se busca complejidad**: se busca que las capas se vean y se puedan explicar.

```
Navegador: index.html + app.js                       ← FRONTEND
        │  HTTP / JSON (fetch)
        ▼
 PRESENTACIÓN   VentaController      recibe y responde  ┐
        │                                                │
        ▼                                                │ BACKEND
 NEGOCIO        VentaService         reglas y cálculo    │ (Spring Boot)
        │                                                │
        ▼                                                │
 DATOS          ProductoRepository   SQL (JdbcTemplate)  │
                VentaRepository                          ┘
        │  JDBC
        ▼
 BASE DE DATOS  H2: tablas producto y venta
```

Cada capa **solo llama a la de abajo**. El frontend nunca toca la BD; el controlador nunca escribe SQL; el repositorio nunca decide reglas de negocio.

## Cómo está organizado (SDD)

Primero se especifica, luego se codea. Una sola spec.

| Ruta | Contenido |
|---|---|
| `AGENTS.md` / `CLAUDE.md` | Reglas para quien implemente (persona o agente de IA) |
| `specs/steering/producto.md` | Qué hace el sistema, alcance y reglas de negocio RN-01 a RN-03 |
| `specs/steering/tecnologia.md` | Stack, justificación y comandos |
| `specs/steering/estructura.md` | Las capas, reglas de dependencia y árbol de archivos |
| `specs/arquitectura/arquitectura.md` | Diagramas (capas y secuencia) y **por qué es Arquitectura en Capas** |
| `specs/01-registrar-venta/` | `requirements.md` → `design.md` → `tasks.md` de la única funcionalidad |
| `specs/demo/guion-video2.md` | Guion del Video 2 |

## Comandos (cuando exista el código)

```bash
./mvnw spring-boot:run
```

App en `http://localhost:8080` · consola de BD en `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:tienda`, usuario `sa`, sin contraseña).

En Windows (PowerShell) usar `.\mvnw.cmd spring-boot:run`.
