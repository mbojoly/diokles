
CURL –X POST \
-H "Accept: application/json" \
-H "Content-Type: application/json" \
-d '{"cpuIntensiveComputationsDuration":100, "databaseCallsNumber":5, "databaseCallDuration":5, "serviceCalls":[{"computationDescription":{"cpuIntensiveComputationsDuration":100, "databaseCallsNumber":5, "databaseCallDuration":5}, "callsNumber":2 }]}' \
http://localhost:8080/compute

How to launch H2
java -cp "C:\Users\mbo\.m2\repository\com\h2database\h2\1.4.187\h2-1.4.187.jar" org.h2.tools.Server


