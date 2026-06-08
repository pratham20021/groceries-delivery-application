FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /build
COPY pom.xml .
COPY auth-service/pom.xml auth-service/pom.xml
RUN mvn dependency:go-offline -pl auth-service -am -q
COPY auth-service/src auth-service/src
RUN mvn package -pl auth-service -DskipTests -q

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /build/auth-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
