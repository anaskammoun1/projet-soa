# syntax=docker/dockerfile:1

FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /workspace

COPY pom.xml .
RUN mvn -B -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring

COPY --from=build /workspace/target/gestion-soutenances-*.jar /app/app.jar

ENV SERVER_PORT=8080

EXPOSE 8080

USER spring

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
