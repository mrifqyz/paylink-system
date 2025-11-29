FROM maven:3.8.4-openjdk-17 AS builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/paylink-system-0.0.1-SNAPSHOT.jar .

EXPOSE 8080

CMD ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -jar paylink-system-0.0.1-SNAPSHOT.jar"]
