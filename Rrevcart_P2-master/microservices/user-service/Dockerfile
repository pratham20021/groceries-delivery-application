FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /build
COPY pom.xml .
COPY user-service/pom.xml user-service/pom.xml
RUN mvn dependency:go-offline -pl user-service -am -q
COPY user-service/src user-service/src
RUN mvn package -pl user-service -DskipTests -q

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /build/user-service/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
