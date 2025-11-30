#!/bin/bash

container_name="postgres-challenge"

# here we check if we already have the image , in case of pc restart/ crash
if [ "$(docker container inspect -f '{{.State.Running}}' $container_name)" = "true" ]; then
  sudo docker start $container_name
else
  docker run --name postgres-challenge \
    -e POSTGRES_PASSWORD=password \
    -p 5432:5432 \
    -d postgres:15
fi
