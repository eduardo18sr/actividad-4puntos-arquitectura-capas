# AGENTS.md · Instrucciones para agentes de código (y para el equipo)

Este repositorio sigue **desarrollo guiado por especificaciones (SDD)**: primero se especifica, luego se codea.

## Antes de escribir código

1. Leer `specs/steering/producto.md`, `specs/steering/tecnologia.md` y `specs/steering/estructura.md`.
2. Leer `specs/arquitectura/arquitectura.md`.
3. Implementar `specs/01-registrar-venta/`: leer `requirements.md` y `design.md`, y ejecutar `tasks.md` tarea por tarea.

## Reglas obligatorias

- **Mantenerlo simple.** Es una actividad corta: una sola funcionalidad (registrar venta). No agregar login, carrito, IGV, perfiles, AOP, ArchUnit ni nada que no esté en la spec. Docker se usa para empaquetar las tres piezas (frontend, backend, base de datos), una imagen cada una; no agregar más servicios.
- Respetar las capas de `specs/steering/estructura.md`: `presentacion` → `negocio` → `datos` → BD. Ninguna capa se salta a otra ni llama hacia arriba.
- El SQL solo vive en `datos`. Las reglas de negocio (RN-01 a RN-03) solo viven en `negocio`. El controlador no calcula nada.
- Si un nombre de clase o ruta cambia respecto al diseño, actualizar `design.md`, `estructura.md` y `arquitectura.md` en el mismo commit: los diagramas deben coincidir con lo que se muestra en el video.
- Código y mensajes en español; dinero con `BigDecimal`; inyección por constructor.

## Comandos

```bash
docker compose up -d --build          # todo en contenedores → http://localhost:8080
docker compose up -d db               # solo la BD, para desarrollar
cd backend && ./mvnw clean spring-boot:run
cd backend && ./mvnw clean test
```
