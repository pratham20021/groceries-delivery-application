FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /build
COPY pom.xml .
COPY notification-service/pom.xml notification-service/pom.xml
RUN mvn dependency:go-offline -pl notification-service -am -q
COPY notification-service/src notification-service/src
RUN mvn package -pl notification-service -DskipTests -q

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /build/notification-service/target/*.jar app.jar
EXPOSE 8087
ENTRYPOINT ["java", "-jar", "app.jar"]
