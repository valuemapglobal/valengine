# ============================================================
# RiskSmart Data Middle Station Service Dockerfile
# Multi-stage build for optimal image size
# ============================================================

# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

# Copy parent pom and all modules
COPY pom.xml .
COPY risk-smart-common/pom.xml risk-smart-common/
COPY risk-smart-system/pom.xml risk-smart-system/
COPY risk-smart-decision-manage/pom.xml risk-smart-decision-manage/
COPY risk-smart-data-middle-station/pom.xml risk-smart-data-middle-station/

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -B -pl risk-smart-data-middle-station -am

# Copy source code
COPY risk-smart-common/src risk-smart-common/src
COPY risk-smart-system/src risk-smart-system/src
COPY risk-smart-data-middle-station/src risk-smart-data-middle-station/src

# Build the project
RUN mvn clean package -DskipTests -pl risk-smart-data-middle-station -am

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="RiskSmart Team"
LABEL description="RiskSmart Data Middle Station Service"

# Set timezone
ENV TZ=Asia/Shanghai
RUN apk add --no-cache tzdata curl && \
    cp /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone

WORKDIR /app

# Copy jar from builder
COPY --from=builder /build/risk-smart-data-middle-station/target/*.jar app.jar

# Create logs directory
RUN mkdir -p /app/logs

# Expose port
EXPOSE 8990

# JVM options
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8990/api/actuator/health || exit 1

# Run application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
