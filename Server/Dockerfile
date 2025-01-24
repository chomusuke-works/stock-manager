FROM alpine:latest

RUN apk add --no-cache openjdk21
COPY target/*-jar-with-dependencies.jar /app/stockmanager.jar

CMD ["java", "-jar", "/app/stockmanager.jar", "--host", "database", "--port", "5432"]