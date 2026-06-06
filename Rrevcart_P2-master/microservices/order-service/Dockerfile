FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /build
COPY pom.xml .
COPY api-gateway/pom.xml api-gateway/
COPY auth-service/pom.xml auth-service/
COPY user-service/pom.xml user-service/
COPY product-service/pom.xml product-service/
COPY cart-service/pom.xml cart-service/
COPY order-service/pom.xml order-service/
COPY payment-service/pom.xml payment-service/
COPY notification-service/pom.xml notification-service/
COPY delivery-service/pom.xml delivery-service/
COPY analytics-service/pom.xml analytics-service/
COPY admin-service/pom.xml admin-service/
RUN mvn dependency:go-offline -pl order-service -q
COPY order-service/src order-service/src
RUN mvn package -pl order-service -DskipTests -q

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /build/order-service/target/*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]
