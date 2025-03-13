FROM openjdk:17-jdk-slim
LABEL authors="sophie"
WORKDIR /app
COPY target/loans-0.0.1-SNAPSHOT.jar ./loans.jar
ENTRYPOINT ["java", "-jar","loans.jar"]