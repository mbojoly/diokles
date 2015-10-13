#!/bin/sh

docker build -t db - < docker/h2-dockerfile
mvn install docker:build