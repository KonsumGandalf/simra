FROM gradle:jdk23-alpine AS builder

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src

RUN gradle build --no-daemon

FROM eclipse-temurin:23-jre-alpine AS production

RUN addgroup -S spring && adduser -S spring -G spring && \
    mkdir -p /app
USER spring:spring

COPY --from=builder /home/gradle/src/build/libs/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=200", "-jar","/app/app.jar"]

