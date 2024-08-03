
FROM openjdk:18-jdk-slim
WORKDIR /app
COPY ./build/libs/account-service.jar /app/account-service.jar
#COPY ./build/libs/accounts-0.0.1-SNAPSHOT-plain.jar /app/account-service.jar
ENV SPRING_DATASOURCE_URL=jdbc:mysql://kigen.mysql.database.azure.com:3306/ayact_africa?useSSL=true&serverTimezone=UTC
ENV SPRING_DATASOURCE_USERNAME=Test
ENV SPRING_DATASOURCE_PASSWORD="Kigen@2024"
ENV SERVER_PORT=3306
EXPOSE 9006
CMD ["java", "-jar", "account-service.jar"]

