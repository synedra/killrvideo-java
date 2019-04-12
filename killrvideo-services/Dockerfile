FROM openjdk:11.0.3-jdk-slim-stretch

MAINTAINER Cedrick Lunven <cedrick.lunven@datastax.com>
MAINTAINER Davig Gilardi  <david.gilardi@datastax.com>


########################################################
## Environment Variables
########################################################
ARG KILLRVIDEO_DOCKER_IP
ENV KILLRVIDEO_DOCKER_IP ${KILLRVIDEO_DOCKER_IP}
ARG KILLRVIDEO_HOST_IP
ENV KILLRVIDEO_HOST_IP ${KILLRVIDEO_HOST_IP}
ARG KILLRVIDEO_DSE_USERNAME
ENV KILLRVIDEO_DSE_USERNAME ${KILLRVIDEO_DSE_USERNAME}
ARG KILLRVIDEO_DSE_PASSWORD
ENV KILLRVIDEO_DSE_PASSWORD ${KILLRVIDEO_DSE_PASSWORD}
ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS

VOLUME /tmp
ARG JAR_FILE
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]

EXPOSE 8899