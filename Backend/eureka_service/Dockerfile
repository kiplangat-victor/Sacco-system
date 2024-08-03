FROM openjdk:11-jdk-slim
WORKDIR /app
COPY ./build/libs/eureka-service.jar /app/eureka-service.jar
#COPY ./build/libs/eureka-0.0.1-SNAPSHOT-plain.jar /app/eureka-service.jar
ENV SPRING_DATASOURCE_URL=jdbc:mysql://kigen.mysql.database.azure.com:3306/emt_multitenant_sacco?useSSL=true&serverTimezone=UTC
ENV SPRING_DATASOURCE_USERNAME=Test
ENV SPRING_DATASOURCE_PASSWORD="Kigen@2024"
ENV SERVER_PORT=3306
EXPOSE 9001
CMD ["java", "-jar", "eureka-service.jar"]

#FROM openjdk:11-jdk-slim
#WORKDIR /app
#COPY ./build/libs/eureka-service.jar /app/eureka-service.jar
## Ensure you are copying the correct JAR file
## COPY ./build/libs/eureka-0.0.1-SNAPSHOT-plain.jar /app/eureka-service.jar
#ENV SPRING_DATASOURCE_URL=jdbc:mysql://kigen.mysql.database.azure.com:3306/emt_multitenant_sacco?useSSL=true&serverTimezone=UTC
#ENV SPRING_DATASOURCE_USERNAME=Test
#ENV SPRING_DATASOURCE_PASSWORD="Kigen@2024"
## Set the correct server port
#ENV SERVER_PORT=3306
#EXPOSE 9001
#CMD ["java", "-jar", "eureka-service.jar"]
