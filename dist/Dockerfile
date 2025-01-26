FROM alpine:latest

RUN apk add --no-cache openjdk21

CMD ["java", "-jar", "/app/stockmanager.jar", "--host", "database", "--port", "5432"]