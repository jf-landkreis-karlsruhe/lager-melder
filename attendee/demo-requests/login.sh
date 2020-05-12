#!/bin/bash
curl -i -H "Content-Type: application/json" -X POST -d '{
    "username": "user",
    "password": "pass"
}' http://localhost:8080/login
