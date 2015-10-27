#DIOKLES : An information system scale performance simulator

[![Build Status](https://travis-ci.org/mbojoly/diokles.svg)](https://travis-ci.org/mbojoly/diokles)

##Description
DIOKLES is a set of docker appliances that allows to simulate on a single virtual machine like docker-machine several 
processes in order to diagnose and better understand large performance problems.

As explained during {{soft-shake}} presentation [Comment tester et optimiser la performance d'un SI](https://github.com/mbojoly/softshake-perf-si)
some end-to-end results can be counter-intuitive when you compare them to the sum of the individual performance results results. Demo at {{soft-shake}} uses DIOKLES
in order to demonstrate the impact of N+1 requests, N+1 applications calls and network latencies at the IS performance level.

More globally, DIOKLES application simulates 
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

In particular, you can investigate N+1 problems errors or add latency to the network through the `./sh/poc2.sh` a `./sh/quizz-on.sh` 
command.

##Getting started
DIOKLES requires and Docker 1.8.2+ with docker-machine to run.
It requires Java8+ and maven 3.3.3+ to build

###RUN
```
cd <DIOKLES HOME>
./sh/start.sh
./sh/poc1.sh <IP of your docker machine>
#See the response time for a single application call
./sh/poc2.sh <IP of your docker machine>
#See the network call for 7 sequential network latency
#Simulates network latencies
./sh/quizz-on.sh <name of you docker machine e.g. default>
#See the new response times
./sh/poc2.sh <IP of your docker machine>
#Simulate your own topology
./sh/quizz-off.sh <name of your docker machine e.g. default>
./sh/stop.sh
```

###BUILD
```
cd <DIOKLES HOME>
./sh/clean.sh
./sh/build-h2.sh
./sh/build.sh
```
