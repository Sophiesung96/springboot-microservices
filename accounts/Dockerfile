FROM openjdk:17-jdk-slim
LABEL authors="sophie"
WORKDIR /app
COPY target/accounts-0.0.1-SNAPSHOT.jar ./accounts.jar
ENTRYPOINT ["java", "-jar","accounts.jar"]






