#!/bin/sh -v

QDISC=$(docker-machine ssh default "sudo /usr/local/sbin/tc qdisc show dev docker0")

if [[ ! -z "$QDISC" ]]
then
	docker-machine ssh default "sudo /usr/local/sbin/tc qdisc del root dev docker0"
fi
