#!/bin/sh

docker-machine ssh default "docker run --privileged=true --net=host --rm corfr/tcpdump -i docker0 -w - > ./log.pcap"