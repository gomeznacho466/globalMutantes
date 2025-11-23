# Etapa de compilación
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Etapa de ejecución (ACÁ CAMBIAMOS LA IMAGEN)
FROM eclipse-temurin:17-jdk-jammy
COPY --from=build /target/mutant-detector-1.0.0.jar app.jar
ENV SERVER_PORT=8080
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
