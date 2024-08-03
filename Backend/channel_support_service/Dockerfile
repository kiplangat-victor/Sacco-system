
FROM openjdk:11-jdk-slim
WORKDIR /app
COPY ./build/libs/channel-support-service.jar /app/channel-support-service.jar
COPY ./build/libs/switch_middleware-0.0.1-SNAPSHOT-plain.jar /app/switch_middleware.jar
ENV SPRING_DATASOURCE_URL=jdbc:mysql://kigen.mysql.database.azure.com:3306/emt_multitenant_sacco?useSSL=true&serverTimezone=UTC
ENV SPRING_DATASOURCE_USERNAME=Test
ENV SPRING_DATASOURCE_PASSWORD="Kigen@2024"
ENV SERVER_PORT=3306
EXPOSE 2024
CMD ["java", "-jar", "channel-support-service.jar"]

