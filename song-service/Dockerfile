# Stage 1: Build environment
# Install Maven build tool
FROM maven:3-eclipse-temurin-17-alpine AS builder
# Set a consistent working directory
WORKDIR /app
# Introduce dependency caching
COPY pom.xml ./
RUN mvn dependency:go-offline
# Copy source code
COPY ./src ./src
# Build
RUN mvn clean package -Dmaven.test.skip=true

# Stage 2: Runtime environment
FROM eclipse-temurin:17-jre-alpine AS final
WORKDIR /app
# Copy JAR file from the build stage
COPY --from=builder /app/target/*.jar ./app.jar
# Indicate the application’s internal port in the runtime stage
EXPOSE 8080
# Define runtime configuration
CMD ["java", "-jar", "/app/app.jar"]