# =============================================================================
# Image Editor - Multi-stage Docker Build
# =============================================================================
# Stage 1: Build stage - compiles and packages the application
# Stage 2: Runtime stage - minimal image for running the application
# =============================================================================

# -----------------------------------------------------------------------------
# Stage 1: Build
# -----------------------------------------------------------------------------
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

# Copy Maven configuration
COPY pom.xml .

# Copy source code
COPY src ./src

# Install Maven and build the application
RUN apt-get update && \
    apt-get install -y maven && \
    mvn clean package -DskipTests && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# -----------------------------------------------------------------------------
# Stage 2: Runtime
# -----------------------------------------------------------------------------
FROM eclipse-temurin:17-jre

LABEL maintainer="Image Editor Project"
LABEL description="Command-line image processing utility"
LABEL version="1.0.0"

# Create non-root user for security
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

WORKDIR /app

# Copy JAR from build stage
COPY --from=builder /app/target/image-editor-1.0.0.jar ./image-editor.jar

# Copy sample image for testing
COPY --from=builder /app/src/main/resources/taylor.jpg ./sample.jpg

# Change ownership to non-root user
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Set entrypoint
ENTRYPOINT ["java", "-jar", "image-editor.jar"]

# Default command shows help
CMD ["--help"]
