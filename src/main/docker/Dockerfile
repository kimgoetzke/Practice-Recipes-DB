# This file describes how Docker should run the Spring Boot application
FROM eclipse-temurin:17-jdk-alpine
ARG JAR_FILE=*.jar
COPY ${JAR_FILE} application.jar
EXPOSE 8881
ENTRYPOINT ["java", "-jar", "application.jar"]