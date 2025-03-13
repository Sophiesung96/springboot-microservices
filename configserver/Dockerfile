FROM openjdk:17-jdk-slim
LABEL authors="sophie"
WORKDIR /app
RUN apt-get update && apt-get install -y curl
COPY target/configserver-0.0.1-SNAPSHOT.jar ./configserver.jar
ENTRYPOINT ["java", "-jar","configserver.jar"]

