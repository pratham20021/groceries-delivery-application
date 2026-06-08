FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /build
COPY pom.xml .
COPY admin-service/pom.xml admin-service/pom.xml
RUN mvn dependency:go-offline -pl admin-service -am -q
COPY admin-service/src admin-service/src
RUN mvn package -pl admin-service -DskipTests -q

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /build/admin-service/target/*.jar app.jar
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "app.jar"]
