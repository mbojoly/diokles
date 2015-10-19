#!/bin/sh -v
docker run -p 9093:9093 -p 81:81 -d db
#Add -e "TRACE_LEVEL=3" if required
docker run -p 8080:8080 -e "DB_HOST=192.168.99.100" -e "DB_PORT=9093" -e "HTTP_HOST=192.168.99.100" -d application
docker ps
