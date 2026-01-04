# ============================================================
# RiskSmart Web Frontend Dockerfile
# Multi-stage build for optimal image size
# ============================================================

# Stage 1: Build
FROM node:20-alpine AS builder

WORKDIR /app

# Build arguments
ARG VITE_API_BASE_URL=/api

# Set environment variables for build
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm ci --registry=https://registry.npmmirror.com

# Copy source code
COPY . .

# Build the application
RUN npm run build

# Stage 2: Production
FROM nginx:alpine

LABEL maintainer="RiskSmart Team"
LABEL description="RiskSmart Web Frontend"

# Set timezone
ENV TZ=Asia/Shanghai
RUN apk add --no-cache tzdata curl && \
    cp /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone

# Remove default nginx config
RUN rm -rf /etc/nginx/conf.d/*

# Copy nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy built files from builder
COPY --from=builder /app/dist /usr/share/nginx/html

# Expose port
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
    CMD curl -f http://localhost/ || exit 1

# Start nginx
CMD ["nginx", "-g", "daemon off;"]
