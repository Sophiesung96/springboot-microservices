FROM openjdk:17-jdk-slim
LABEL authors="sophie"
COPY target/loans-0.0.1-SNAPSHOT.jar loans.jar
ENTRYPOINT ["java", "-jar","loans.jar"]