#!/bin/sh

if [[ ! -z $1 ]] 
then
	HOST=$1
else
	HOST=192.168.99.100
fi
	

curl -X POST \
-H "Accept: applicaiton/json" \
-H "Content-Type: application/json" \
-d '{"cpuIntensiveComputationsDuration":60, "databaseCallsNumber":6, "databaseCallDuration":10 }' \
http://$HOST:8080/compute