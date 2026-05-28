# Etapa 1: Build (Construcción del JAR)
FROM maven:3.9-eclipse-temurin-17-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Runtime (Ejecución optimizada)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# Exponer el puerto por defecto de Spring Boot
EXPOSE 8080

# Configuración de zona horaria (Colombia) y ejecución
ENV TZ=America/Bogota
ENTRYPOINT ["java", "-jar", "/app/app.jar"]