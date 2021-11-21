#!/bin/bash

curl -i -H "Content-Type: application/json" -H "Authorization: Bearer $1" -X POST -d '{
    "firstName": "'"$2"'",
    "lastName": "'"$3"'",
    "departmentId" : "'"$4"'",
    "birthday": "08-05-2019",
    "food": "MEAT",
    "tShirtSize": "M",
    "additionalInformation": "nix",
    "role": "YOUTH"
}' http://localhost:8080/attendees

