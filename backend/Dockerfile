# Multi-stage build for Spring Boot backend on Render

FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copy backend pom and sources
COPY pom.xml ./
COPY src ./src

# Build jar (skip tests)
RUN mvn -DskipTests clean package

# Runtime image
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy fat jar from builder
COPY --from=build /workspace/target/*SNAPSHOT.jar /app/app.jar

ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080

ENTRYPOINT ["java","-Xms256m","-Xmx512m","-jar","/app/app.jar"]

