# ---------- Stage 1: build ----------
# Full JDK + Maven, only used to compile and package the JAR
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /src
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
ARG APP_VERSION=1.0.0
RUN mvn -B -q -DskipTests -Drevision=${APP_VERSION} package \
    && cp target/todo-api-*.jar /src/app.jar

# ---------- Stage 2: runtime ----------
# Slim JRE only: no Maven, no JDK, no source code in the final image
FROM eclipse-temurin:17-jre
WORKDIR /app
RUN useradd --system appuser
COPY --from=build /src/app.jar app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
