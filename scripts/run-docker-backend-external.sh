#!/bin/bash

# setting environment variable to the IP address of the host
export KILLRVIDEO_BACKEND=`ipconfig getifaddr en0`

# the compose file swaps in the value of `KILLRVIDEO_BACKEND` in several places
docker-compose -p killrvideo-java -f docker-compose-backend-external.yaml up -d
 