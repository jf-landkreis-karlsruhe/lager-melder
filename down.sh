#!/bin/sh
docker-compose -f ./backend/docker-compose/docker-compose.yml down
rm -rf ./backend/target
