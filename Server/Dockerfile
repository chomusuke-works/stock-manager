FROM alpine:latest

RUN apk add --no-cache openjdk21
COPY target/*.jar /app/stockmanager.jar

CMD ["java", "-jar", "/app/stockmanager.jar"]