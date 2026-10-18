# BACKEND · Capas de presentación (API REST), negocio y datos en una imagen.
# Se construye desde la raíz del repo (docker compose lo hace): necesita backend/ y los scripts de base-de-datos/ para las pruebas.

# 1) Compilar, correr las pruebas (H2 en memoria) y empaquetar el .jar
FROM maven:3.9-eclipse-temurin-21 AS compilacion
WORKDIR /app/backend
COPY backend/pom.xml .
RUN mvn -B -q dependency:go-offline
COPY base-de-datos/schema.sql base-de-datos/data.sql /app/base-de-datos/
COPY backend/src ./src
RUN mvn -B -q package

# 2) Imagen final: solo el JRE y el .jar
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S tienda && adduser -S tienda -G tienda
COPY --from=compilacion /app/backend/target/tiendacapas-*.jar app.jar
USER tienda

ENV TZ=America/Lima \
    DB_URL=jdbc:postgresql://db:5432/tienda \
    DB_USUARIO=tienda \
    DB_CLAVE=tienda

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
