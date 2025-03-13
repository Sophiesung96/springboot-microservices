FROM openjdk:17-jdk-slim
LABEL authors="sophie"
WORKDIR /app
COPY target/cards-0.0.1-SNAPSHOT.jar ./cards.jar
ENTRYPOINT ["java", "-jar","cards.jar"]