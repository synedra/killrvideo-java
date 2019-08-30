FROM openjdk:11

MAINTAINER Cedrick Lunven <cedrick.lunven@datastax.com>
MAINTAINER Davig Gilardi  <david.gilardi@datastax.com>

########################################################
## Environment Variables
########################################################

ARG KILLRVIDEO_DSE_CONTACT_POINTS
ENV KILLRVIDEO_DSE_CONTACT_POINTS ${KILLRVIDEO_DSE_CONTACT_POINTS}

ARG KILLRVIDEO_DSE_USERNAME
ENV KILLRVIDEO_DSE_USERNAME ${KILLRVIDEO_DSE_USERNAME}

ARG KILLRVIDEO_DSE_PASSWORD
ENV KILLRVIDEO_DSE_PASSWORD ${KILLRVIDEO_DSE_PASSWORD}

VOLUME /tmp
ARG JAR_FILE
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]

# Exposing expected port by WEBUI
EXPOSE 50101
