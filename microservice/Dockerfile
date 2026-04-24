FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/microservice-0.0.1-SNAPSHOT.jar app.jar
RUN mkdir -p /app/data
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
