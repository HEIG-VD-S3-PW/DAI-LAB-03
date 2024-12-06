# Utilisez l'image officielle OpenJDK comme base
FROM openjdk:21-jdk-slim

# Répertoire de travail
WORKDIR /app

# Copie du code compilé (JAR) dans l'image
COPY target/DAI-LAB-03-1.0-SNAPSHOT.jar /app/DAI-LAB-03-1.0-SNAPSHOT.jar

# Copie des vidéos pour le serveur
COPY videos /app/videos

# Exposer le port 1986
EXPOSE 1986

# Commande par défaut (serveur)
CMD ["java", "-jar", "/app/DAI-LAB-03-1.0-SNAPSHOT.jar", "server"]

