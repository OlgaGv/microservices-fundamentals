# Stage 1: Build Environment
# Install build tools (e.g., Maven, Gradle)
FROM maven:3-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml ./
RUN mvn dependency:go-offline
# Copy source code
COPY ./src ./src
# Build commands (e.g., compile, package)
RUN mvn clean package -Dmaven.test.skip=true

# Stage 2: Runtime environment
FROM eclipse-temurin:17-jre AS final
WORKDIR /app
#  Copy application artifacts from the build stage (e.g., JAR file)
# COPY --from=build-stage /path/in/build/stage /path/to/place/in/final/stage
COPY --from=builder /app/target/*.jar ./api-gateway.jar
EXPOSE 8765
# Define runtime configuration (e.g., CMD, ENTRYPOINT)
CMD ["java", "-jar", "/app/api-gateway.jar"]
