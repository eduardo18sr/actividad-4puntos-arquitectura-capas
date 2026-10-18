# AGENTS.md · Instrucciones para agentes de código (y para el equipo)

Este repositorio sigue **desarrollo guiado por especificaciones (SDD)**: primero se especifica, luego se codea. El código se escribe **solo** a partir de las specs de `specs/`.

## Antes de escribir código

1. Leer los tres archivos de steering: `specs/steering/producto.md`, `specs/steering/tecnologia.md` y `specs/steering/estructura.md`.
2. Leer `specs/arquitectura/arquitectura.md`.
3. Trabajar **una spec a la vez**, en orden: `00-fundaciones-capas` → `01-catalogo` → `02-carrito` → `03-registrar-venta` → `04-consultar-ventas`.
4. Dentro de una spec: leer `requirements.md` y `design.md`, y ejecutar `tasks.md` **tarea por tarea**, en orden.

## Reglas obligatorias

- Respetar las reglas de capas AR-01 a AR-07 de `specs/steering/estructura.md`. La presentación **nunca** usa clases del dominio ni de la persistencia; el dominio **nunca** importa Spring, JDBC ni Jakarta.
- No implementar nada que no esté en una spec. Si falta algo, proponer el cambio en la spec antes de codear.
- Cada tarea termina con `./mvnw test` en verde (incluido `ArquitecturaCapasTest`) y su casilla marcada `[x]` en el `tasks.md`.
- Si un nombre de clase, ruta o paquete cambia respecto al diseño, actualizar en el mismo commit `design.md`, `estructura.md` y `arquitectura.md`: los diagramas deben coincidir con el código que se muestra en el video.
- Código y mensajes al usuario en español; dinero con `BigDecimal`; inyección por constructor.

## Comandos

```bash
./mvnw spring-boot:run
./mvnw spring-boot:run -Dspring-boot.run.profiles=memoria
./mvnw test
```
