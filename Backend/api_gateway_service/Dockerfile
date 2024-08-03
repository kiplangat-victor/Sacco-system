
FROM openjdk:11-jdk-slim
WORKDIR /app
COPY ./build/libs/sacco-gateway-service.jar /app/sacco-gateway-service.jar
#COPY ./build/libs/switch_middleware-0.0.1-SNAPSHOT-plain.jar /app/sacco-gateway-service.jar
ENV SPRING_DATASOURCE_URL=jdbc:mysql://kigen.mysql.database.azure.com:3306/ayact_africa?useSSL=true&serverTimezone=UTC
ENV SPRING_DATASOURCE_USERNAME=Test
ENV SPRING_DATASOURCE_PASSWORD="Kigen@2024"
ENV SERVER_PORT=3306
EXPOSE 9002
CMD ["java", "-jar", "sacco-gateway-service.jar"]

