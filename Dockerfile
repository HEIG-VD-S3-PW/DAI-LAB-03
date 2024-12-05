# Use an official OpenJDK image as a base image
FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/DAI-LAB-03-1.0-SNAPSHOT.jar /app/DAI-LAB-03-1.0-SNAPSHOT.jar

EXPOSE 1986

ENTRYPOINT ["java", "-jar", "/app/DAI-LAB-03-1.0-SNAPSHOT.jar"]
