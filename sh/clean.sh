#!/bin/sh

TARGET=$1
if [[ ! -z "$TARGET" ]]
then
	echo "Clean $TARGET"
else
	TARGET=diokles-application
	echo "Clean $TARGET"
fi

DOCKER_PROC=$(docker ps -q)
if [[ ! -z "$DOCKER_PROC" ]] 
then
	docker stop $DOCKER_PROC
else
	echo "No docker process running"
fi

DOCKER_CNTR=$(docker ps -a -q)
if [[ ! -z "$DOCKER_CNTR" ]] 
then
	docker rm $DOCKER_CNTR
else
	echo "No docker containers to remove"
fi

RemoveImages() {
	if [[ ! -z $1 ]] 
	then
		docker rmi $1
	else
		echo "No docker images to delete"
	fi
}

RemoveImages $(docker images | grep '<none>' | tr -s ' ' | cut -d ' ' -f 3)
RemoveImages $(docker images | grep "$TARGET" | tr -s ' ' | cut -d ' ' -f 3)

mvn clean

echo "End clean"