
FROM openjdk:18-jdk-slim
WORKDIR /app
COPY ./build/libs/crm-service.jar /app/crm-service.jar
#COPY ./build/libs/customers-0.0.1-SNAPSHOT-plain.jar /app/crm-service.jar
ENV SPRING_DATASOURCE_URL=jdbc:mysql://kigen.mysql.database.azure.com:3306/ayact_africa?useSSL=true&serverTimezone=UTC
ENV SPRING_DATASOURCE_USERNAME=Test
ENV SPRING_DATASOURCE_PASSWORD="Kigen@2024"
ENV SERVER_PORT=3306
EXPOSE 9005

CMD ["java", "-jar", "crm-service.jar"]

