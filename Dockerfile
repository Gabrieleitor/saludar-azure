# syntax=docker/dockerfile:1

FROM eclipse-temurin:25-jdk-noble AS builder

WORKDIR /app

COPY gradlew settings.gradle build.gradle ./
COPY gradle gradle
RUN chmod +x gradlew

COPY src src

RUN ./gradlew bootJar --no-daemon

# El JAR ejecutable no lleva el sufijo "-plain"
RUN cp "$(ls build/libs/*.jar | grep -v -- '-plain\.jar$' | head -n 1)" application.jar

FROM eclipse-temurin:25-jre-noble

WORKDIR /app

RUN useradd --uid 1001 --user-group --no-create-home app

COPY --from=builder /app/application.jar application.jar

USER app

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "application.jar"]
