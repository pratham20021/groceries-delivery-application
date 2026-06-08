FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /build
COPY pom.xml .
COPY analytics-service/pom.xml analytics-service/pom.xml
RUN mvn dependency:go-offline -pl analytics-service -am -q
COPY analytics-service/src analytics-service/src
RUN mvn package -pl analytics-service -DskipTests -q

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /build/analytics-service/target/*.jar app.jar
EXPOSE 8089
ENTRYPOINT ["java", "-jar", "app.jar"]
