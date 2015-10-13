#!/bin/sh
curl -X POST \
-H "Accept: applicaiton/json" \
-H "Content-Type: application/json" \
-d '{"cpuIntensiveComputationsDuration":50, "databaseCallsNumber":5, "databaseCallDuration":10, "serviceCalls":[{"computationDescription":{"cpuIntensiveComputationsDuration":50, "databaseCallsNumber":5, "databaseCallDuration":5}, "callsNumber":2 }, {"computationDescription":{"cpuIntensiveComputationsDuration":50, "databaseCallsNumber":5, "databaseCallDuration":5}, "callsNumber":2 }, {"computationDescription":{"cpuIntensiveComputationsDuration":50, "databaseCallsNumber":5, "databaseCallDuration":5}, "callsNumber":2 }, {"computationDescription":{"cpuIntensiveComputationsDuration":50, "databaseCallsNumber":5, "databaseCallDuration":5}, "callsNumber":2 }]}' \
http://192.168.99.100:8080/compute