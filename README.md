java -jar target/microservices-1.0-SNAPSHOT.jar server microservices.yml

curl -X POST \
-H "Accept: applicaiton/json" \
-H "Content-Type: application/json" \
-d '{"cpuIntensiveComputationsDuration":100, "databaseCallsNumber":5, "databaseCallDuration":5, "serviceCalls":[{"computationDescription":{"cpuIntensiveComputationsDuration":100, "databaseCallsNumber":5, "databaseCallDuration":5}, "callsNumber":2 }]}' \
http://localhost:8080/compute



How to run on docker
docker-machine ls
DOCKER_HOST=tcp://192.168.99.100:2376
mvn docker:build
docker run -p 8080:8080 -d microservices
curl -X POST \
-H "Accept: applicaiton/json" \
-H "Content-Type: application/json" \
-d '{"cpuIntensiveComputationsDuration":100, "databaseCallsNumber":5, "databaseCallDuration":5, "serviceCalls":[{"computationDescription":{"cpuIntensiveComputationsDuration":100, "databaseCallsNumber":5, "databaseCallDuration":5}, "callsNumber":2 }]}' \
http://192.168.99.100:8080/compute
doker ps
docker logs -f grave_sammet
docker kill grave_sammet
docker rm grave_sammet

To run in a docker container with a local H2 database
java -cp "C:\Users\mbojoly\.m2\repository\com\h2database\h2\1.4.187\h2-1.4.187.jar" org.h2.tools.Server -tcpPort 9093 -tcpAllowOthers
docker run -p 8080:8080 -e "DB_HOST=192.168.99.1" -e "DB_PORT=9093" -d microservices
