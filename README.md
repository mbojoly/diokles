java -jar target/microservices-1.0-SNAPSHOT.jar server microservices.yml

curl -X POST \
-H "Accept: applicaiton/json" \
-H "Content-Type: application/json" \
-d '{"cpuIntensiveComputationsDuration":100, "databaseCallsNumber":5, "databaseCallDuration":5, "serviceCalls":[{"computationDescription":{"cpuIntensiveComputationsDuration":100, "databaseCallsNumber":5, "databaseCallDuration":5}, "callsNumber":2 }]}' \
http://localhost:8080/compute

How to launch H2
java -cp "C:\Users\mbojoly\.m2\repository\com\h2database\h2\1.4.187\h2-1.4.187.jar" org.h2.tools.Server -tcpPort 9093

TODO : Corriger INTERNAL n'est pas un nom d'host valid