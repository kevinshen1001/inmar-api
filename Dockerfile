# --- Build stage ---
# eclipse-temurin is multi-arch: works on amd64 AND arm64 (Apple Silicon)
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Install Gradle 8.6 directly (avoids the gradle:X-alpine image which lacks arm64 variants)
ARG GRADLE_VERSION=8.6
RUN apt-get update -qq && apt-get install -y --no-install-recommends curl unzip \
    && curl -fsSL "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" \
       -o /tmp/gradle.zip \
    && unzip -q /tmp/gradle.zip -d /opt \
    && ln -s "/opt/gradle-${GRADLE_VERSION}/bin/gradle" /usr/local/bin/gradle \
    && rm /tmp/gradle.zip \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

# Copy build files first for better layer caching
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle/ gradle/

# Pre-fetch dependencies (cache layer — only re-runs when build files change)
RUN gradle dependencies --no-daemon --quiet || true

# Copy source and build the fat jar
COPY src/ src/
RUN gradle bootJar --no-daemon -x test --quiet

# --- Runtime stage ---
# JRE-only slim image, also multi-arch (amd64 + arm64)
FROM eclipse-temurin:17-jre AS runtime
WORKDIR /app

# Non-root user for security
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

# Writable logs directory owned by non-root user
RUN mkdir -p /app/logs && chown -R appuser:appgroup /app/logs

COPY --from=build /app/build/libs/*.jar app.jar
RUN chown appuser:appgroup app.jar

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
