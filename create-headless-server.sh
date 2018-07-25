#!/bin/bash

# This is a simple helper script that creates a Docker image of the 
# killrvideo-java application to run in "headless" mode.
version=$1

if [[ -z "$1" ]]; then
  version="latest"
fi

docker build -t killrvideo/killrvideo-java-server:$version .

# I wasn't kidding, it's really simple
