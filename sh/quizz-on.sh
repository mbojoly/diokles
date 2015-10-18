#!/bin/sh
docker-machine ssh default
QDISC=$(sudo tc qdisc show dev docker0)

if [[ -z $QDISC ]] then
	echo $QDISC
else
	sudo tc qdisc add dev docker0 root netem delay 10ms
	sudo tc qdisc show dev docker0
fi
exit