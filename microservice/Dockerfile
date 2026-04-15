FROM maven:3.9.4-eclipse-temurin-17 as build
WORKDIR /app
COPY pom.xml mvnw settings.xml .
COPY .mvn .mvn
COPY src src
RUN mvn -B -DskipTests -s settings.xml package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/microservice-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
