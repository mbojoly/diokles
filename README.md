java -jar target/diokles-1.0-SNAPSHOT.jar server diokles.yml

curl -X POST \
-H "Accept: applicaiton/json" \
-H "Content-Type: application/json" \
-d '{"cpuIntensiveComputationsDuration":100, "databaseCallsNumber":5, "databaseCallDuration":5, "serviceCalls":[{"computationDescription":{"cpuIntensiveComputationsDuration":100, "databaseCallsNumber":5, "databaseCallDuration":5}, "callsNumber":2 }]}' \
http://localhost:8080/compute

#How to clean
docker stop $(docker ps -q)
docker rm $(docker ps -a -q)
docker rmi $(docker images | grep '<none>' | tr -s ' ' | cut -d ' ' -f 3)

#How to run on docker

docker-machine ls
DOCKER_HOST=tcp://192.168.99.100:2376
mvn clean install docker:build
docker run -p 8080:8080 -d application
curl -X POST \
-H "Accept: applicaiton/json" \
-H "Content-Type: application/json" \
-d '{"cpuIntensiveComputationsDuration":100, "databaseCallsNumber":5, "databaseCallDuration":5, "serviceCalls":[{"computationDescription":{"cpuIntensiveComputationsDuration":100, "databaseCallsNumber":5, "databaseCallDuration":5}, "callsNumber":2 }]}' \
http://192.168.99.100:8080/compute
doker ps
docker logs -f grave_sammet
docker kill grave_sammet
docker rm grave_sammet

# Docker with local H2
To run in a docker container with a local H2 database
java -cp "C:\Users\mbojoly\.m2\repository\com\h2database\h2\1.4.187\h2-1.4.187.jar" org.h2.tools.Server -tcpPort 9093 -tcpAllowOthers
docker run -p 8080:8080 -e "DB_HOST=192.168.99.1" -e "DB_PORT=9093" -d application

# Docker H2

cd docker
docker build -t db - < h2-dockerfile
docker run -p 9093:9093 -p 81:81 -d db

# Docker with docker H2
docker run -p 9093:9093 -p 81:81 -d db
docker run -p 8080:8080 -e "DB_HOST=192.168.99.100" -e "DB_PORT=9093" -d application


# Add latency

curl -X POST \
-H "Accept: applicaiton/json" \
-H "Content-Type: application/json" \
-d '{"databaseCallsNumber":1, "databaseCallDuration":10 }' \
http://192.168.99.100:8080/compute

$ sudo docker-machine ssh default
$$ sudo tc qdisc add dev docker0 root netem delay 10ms
$$ sudo tc qdisc show dev docker0

curl -X POST \
-H "Accept: applicaiton/json" \
-H "Content-Type: application/json" \
-d '{"databaseCallsNumber":1, "databaseCallDuration":10 }' \
http://192.168.99.100:8080/compute


$ docker-machine ssh default
$$ docker run --privileged=true --net=host --rm corfr/tcpdump -i docker0 -w - > ./log.pcap
$$ exit
$ docker-machine scp default:/home/docker/log.pcap .

$$ sudo tc qdisc del root dev docker0

$ docker-machine scp ./sh/quizz-on.sh default:/home/docker/quizz-on.sh

References
https://github.com/spotify/docker-maven-plugin
https://docs.docker.com/reference/run/#env-environment-variablesnot 
http://blog.revollat.net/monitoring-a-distance-du-trafic-reseau-des-containers-docker-avec-wireshark-sous-windows/

https://hub.docker.com/r/corfr/tcpdump/