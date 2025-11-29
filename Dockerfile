FROM maven:3.8.4-openjdk-17 AS builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -B clean package -DskipTests

FROM openjdk:17-alpine

WORKDIR /app

COPY --from=builder /app/target/paylink-system-0.0.1-SNAPSHOT.jar .

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "paylink-system-0.0.1-SNAPSHOT.jar"]
