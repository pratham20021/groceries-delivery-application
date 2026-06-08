FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /build
COPY pom.xml .
COPY product-service/pom.xml product-service/pom.xml
RUN mvn dependency:go-offline -pl product-service -am -q
COPY product-service/src product-service/src
RUN mvn package -pl product-service -DskipTests -q

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /build/product-service/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
