#DIOKLES : An information system scale performance simulator

[![Build Status](https://travis-ci.org/mbojoly/diokles.svg)](https://travis-ci.org/mbojoly/diokles)

##Description
DIOKLES is a set of docker appliance that allows to simulate on a single virtual machine like docker-machine several 
processes in order to diagnose and better understand large performance problems.

DIOKLES application simulates 
- CPU intensive task
- Call to a database
- HTTP call to another DIOKLES application instance

DIOKLES interface is a single `/compute` resource with such kind of syntax:
```
 curl -X POST \
 -H "Accept: applicaiton/json" \
 -H "Content-Type: application/json" \
 -d '{  "cpuIntensiveComputationsDuration":70, \
        "databaseCallsNumber":7, \
        "databaseCallDuration":14, \ 
 "serviceCalls":[ \
   {"computationDescription":{ \
        "cpuIntensiveComputationsDuration":70, \ 
        "databaseCallsNumber":7, \
        "databaseCallDuration":14 \
        }, \
        "callsNumber":6 \
    } \ 
  ]}' \
 http://$HOST:8080/compute
```
Making the different parameters vary allows to see the impact on the `/compute` resource response time. DIOKLES
can be a great tool to learn how to use an APM tool.

##Getting started
DIOKLES requires Java8+ and Docker 1.8.2+ with docker-machine and maven 3.3.3+
```
cd <DIOKLES HOME>
./sh/clean.sh
./sh/build-h2.sh
./sh/build.sh
./sh/start.sh
./sh/poc1.sh
./sh/poc2.sh
#Simulates network latencies
./sh/quizz-on.sh
./sh/poc2.sh
./sh/quizz-off.sh
./sh/stop.sh
```

