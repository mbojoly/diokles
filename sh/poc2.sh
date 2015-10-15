#!/bin/sh
curl -X POST \
-H "Accept: applicaiton/json" \
-H "Content-Type: application/json" \
-d '{"cpuIntensiveComputationsDuration":60, "databaseCallsNumber":6, "databaseCallDuration":12, "serviceCalls":[{"computationDescription":{"cpuIntensiveComputationsDuration":60, "databaseCallsNumber":6, "databaseCallDuration":5}, "callsNumber":2 }, {"computationDescription":{"cpuIntensiveComputationsDuration":60, "databaseCallsNumber":6, "databaseCallDuration":5}, "callsNumber":2 }, {"computationDescription":{"cpuIntensiveComputationsDuration":60, "databaseCallsNumber":6, "databaseCallDuration":5}, "callsNumber":2 }, {"computationDescription":{"cpuIntensiveComputationsDuration":60, "databaseCallsNumber":6, "databaseCallDuration":5}, "callsNumber":2 }, {"computationDescription":{"cpuIntensiveComputationsDuration":60, "databaseCallsNumber":6, "databaseCallDuration":5}, "callsNumber":2 }]}' \
http://192.168.99.100:8080/compute