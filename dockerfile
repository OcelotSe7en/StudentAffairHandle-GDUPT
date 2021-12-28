FROM openjdk:17
MAINTAINER Ocelot_Lau
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar","--spring.config.location=/conf/application.yml"]