FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /build
COPY pom.xml .
COPY cart-service/pom.xml cart-service/pom.xml
RUN mvn dependency:go-offline -pl cart-service -am -q
COPY cart-service/src cart-service/src
RUN mvn package -pl cart-service -DskipTests -q

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /build/cart-service/target/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
