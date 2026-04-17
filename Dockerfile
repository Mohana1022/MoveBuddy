# Build Stage
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Diagnostic: List files to ensure pom.xml is in the right place
RUN ls -la
RUN mvn clean package -DskipTests

# Run Stage
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "app.jar"]
