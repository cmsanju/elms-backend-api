FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /elms-backend

COPY . .

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21

WORKDIR /elms-backend

COPY --from=build /elms-backend/target/*.jar elms-backend-1.0.0.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","elms-backend-1.0.0.jar"]
