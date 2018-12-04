FROM openjdk:8-jdk-alpine
# Add Maintainer Info
LABEL maintainer="ohidalgoleal@gmail.com"

# Add a volume pointing to /tmp
VOLUME /tmp

# Make port 8001 available to the world outside this container
EXPOSE 8003

# Fix zona horaria de Chile
#RUN apk update && apk install -y tzdata
#ENV TZ America/Santiago

ENV CONFIG_SERVER_DMINUTE=http://172.17.0.6:8888
ENV EUREKA_ENDPOINT=http://172.17.0.7:1111/eureka
ENV DOMAIN_NAME=huelen.diinf.usach.cl

# The application's jar file
ARG JAR_FILE=target/d-minute-ms-1.0.0.jar

# Add the application's jar to the container
ADD ${JAR_FILE} d-minute-ms.jar

# Run the jar file 
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=docker","-jar","/d-minute-ms.jar"]