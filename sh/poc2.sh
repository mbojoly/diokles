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
-d '{"cpuIntensiveComputationsDuration":70, "databaseCallsNumber":7, "databaseCallDuration":14, "serviceCalls":[{"computationDescription":{"cpuIntensiveComputationsDuration":70, "databaseCallsNumber":7, "databaseCallDuration":14}, "callsNumber":6 }]}' \
http://$HOST:8080/compute