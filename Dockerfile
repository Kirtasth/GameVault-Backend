# ================================================
# STAGE 1: BUILD LAYER
# ================================================

# We use a Maven image with JDK 21 to compile the application
FROM maven:3.9.6-eclipse-temurin-21 as builder

WORKDIR /build

# Copy only the pom.xml to cache the dependencies in Docker
COPY  pom.xml .

# Download dependencies (offline mode)
RUN mvn dependency:go-offline

# Copy the code
COPY src ./src

# Build the application. We skip tests because Github Actions already ran them
RUN mvn clean package -DskipTests

# ================================================
# STAGE 2: RUNTIME LAYER
# ================================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create a non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring

# Copy the built JAR from the `builder` stage
COPY --from=builder /build/target/*.jar app.jar

# Set ownership to the non-root user
USER spring:spring

# Expose the port
EXPOSE 8080

# Runtime configuration (with less memory)
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]