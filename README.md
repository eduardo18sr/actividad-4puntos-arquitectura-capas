# TiendaCapas · Arquitectura en Capas

Actividad grupal de Ingeniería de Software II (2026-2) · **Parte C: implementación y demostración en vivo**.
Estilo asignado: **Arquitectura en Capas**. Caso acotado: **página web de ventas**. Tecnología: **Java 21 + Spring Boot**.

El enunciado completo de la actividad está en `ISW2_Actividad_Grupal_Arquitecturas_Software (1).md`.

## Cómo está organizado (SDD)

El proyecto se construye con **desarrollo guiado por especificaciones**: requisitos → diseño → tareas, una spec por funcionalidad. Todavía **no hay código**; se escribe siguiendo las specs en orden.

| Ruta | Contenido |
|---|---|
| `AGENTS.md` / `CLAUDE.md` | Reglas para quien implemente (persona o agente de IA) |
| `specs/steering/producto.md` | Qué es la tienda, alcance, actores, **reglas de negocio RN-01 a RN-10**, glosario |
| `specs/steering/tecnologia.md` | Stack, justificación de cada elección, comandos y configuración |
| `specs/steering/estructura.md` | Las capas, **reglas de dependencia AR-01 a AR-07**, árbol de paquetes, convenciones |
| `specs/arquitectura/arquitectura.md` | Metas, escenarios de calidad, restricciones, diagramas (capas, dominio, secuencia, C4), decisiones y **por qué es Arquitectura en Capas** |
| `specs/00-fundaciones-capas/` | Esqueleto, ArchUnit, perfiles de persistencia, traza transversal, errores, semilla |
| `specs/01-catalogo/` | Listar, buscar, filtrar y ver productos |
| `specs/02-carrito/` | Carrito con descuento e IGV |
| `specs/03-registrar-venta/` | Checkout transaccional y comprobante (flujo de la demo) |
| `specs/04-consultar-ventas/` | Ventas registradas (vista del vendedor) |
| `specs/demo/guion-video2.md` | Guion minuto a minuto del Video 2 |

Cada carpeta de spec tiene `requirements.md` (historias + criterios EARS), `design.md` (componentes por capa, contratos, SQL, pruebas) y `tasks.md` (tareas con casillas y trazabilidad a requisitos).

## Las capas en una imagen

```
Navegador ──HTTP──► PRESENTACIÓN  (Spring MVC + Thymeleaf, carrito en sesión)
                         │ DTO
                         ▼
                    APLICACIÓN    (un servicio por caso de uso, @Transactional)
                         │
                         ▼
                      DOMINIO     (reglas de negocio + interfaces DAO, Java puro)
                         ▲
                         │ implementa
                    PERSISTENCIA  (JDBC + H2  |  en memoria, por perfil)

TRANSVERSAL: traza por capa (AOP) y excepción base de negocio
```

## Comandos (cuando exista el código)

```bash
./mvnw spring-boot:run
```

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=memoria
```

```bash
./mvnw test
```
