FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY mvnw pom.xml ./
COPY .mvn ./.mvn
RUN chmod +x ./mvnw && ./mvnw install
COPY src ./src
RUN ./mvnw package
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/DAI-LAB-03-1.0-SNAPSHOT.jar /app/DAI-LAB-03-1.0-SNAPSHOT.jar
VOLUME ["/app/server_data", "/app/client_data"]
ENTRYPOINT ["java", "-jar", "./DAI-LAB-03-1.0-SNAPSHOT.jar"]
