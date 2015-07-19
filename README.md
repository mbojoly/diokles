
CURL –X POST \
-H "Accept: application/json" \
-H "Content-Type: application/json" \
-d '{"cpuIntensiveComputationsDuration":100, "databaseCallsNumber":5, "databaseCallDuration":5, "serviceCalls":[{"computationDescription":{"cpuIntensiveComputationsDuration":100, "databaseCallsNumber":5, "databaseCallDuration":5}, "callsNumber":2 }]}' \
http://localhost:8080/compute



