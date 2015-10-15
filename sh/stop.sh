#!/bin/sh

DOCKER_PROC=$(docker ps -q)
if [[ ! -z "$DOCKER_PROC" ]] 
then
	docker stop $DOCKER_PROC
else
	echo "No docker process running"
fi
