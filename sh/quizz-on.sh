#!/bin/sh -v

if [[ ! -z $1 ]]
then
	MACHINE=$1
else
	MACHINE=default
fi

QDISC=$(docker-machine ssh $MACHINE "sudo /usr/local/sbin/tc qdisc show dev docker0")

if [[ ! -z "$QDISC" ]]
then
	docker-machine ssh default "sudo /usr/local/sbin/tc qdisc del root dev docker0"
fi

docker-machine ssh default "sudo /usr/local/sbin/tc qdisc add dev docker0 root netem delay 10ms"

