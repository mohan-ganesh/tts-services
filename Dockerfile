# --- Build Stage ---
# Use a Maven image to build the application and create an executable JAR.
FROM maven:3.8-openjdk-17 AS build

WORKDIR /app

# Copy pom.xml and download dependencies. This is cached by Docker.
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of the source code and build the application.
COPY src ./src
RUN mvn package -DskipTests

# --- Run Stage ---
# Use a minimal JRE image for a smaller and more secure final container.
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copy the executable JAR from the build stage.
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Run the application. Spring Boot will automatically use the PORT environment variable.
ENTRYPOINT ["java", "-jar", "app.jar"]