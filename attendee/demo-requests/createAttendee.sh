#!/bin/bash

curl -i -H "Content-Type: application/json" -H "Authorization: Bearer $1" -X POST -d '{
    "firstName": "'"$2"'",
    "lastName": "'"$3"'",
    "departmentId" : "'"$4"'"
}' http://localhost:8080/attendee

