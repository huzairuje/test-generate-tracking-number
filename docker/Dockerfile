# Stage 1: Build the JAR using Maven
FROM maven:3.8.6-eclipse-temurin-17 AS builder
WORKDIR /build
COPY pom.xml .
# Download dependencies to cache them (faster rebuilds)
RUN mvn dependency:go-offline
COPY src ./src
# Build the application (skip tests for faster builds; remove -DskipTests to run tests)
RUN mvn package -DskipTests

# Stage 2: Create lightweight runtime image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Copy JAR from the builder stage
COPY --from=builder /build/target/*.jar ./app.jar
# Run as non-root user for security
RUN addgroup --system javauser && adduser -S -s /bin/false -G javauser javauser
USER javauser
ENTRYPOINT ["java", "-jar", "/app/app.jar"]