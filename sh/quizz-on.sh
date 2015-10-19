#!/bin/sh

docker-machine ssh default "sudo /usr/local/sbin/tc qdisc show dev docker0"
