FROM openjdk:11-jre-slim
EXPOSE 8080
RUN mkdir /app
COPY build/libs/chair-app.jar /app/spring-boot-application.jar
ENTRYPOINT ["java","-jar","/app/spring-boot-application.jar"]



