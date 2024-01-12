#!/bin/bash
curl -i -H "Content-Type:application/json" -H "Authorization: Bearer $1" -X POST -d '{
  "sendTo": "DEPARTMENTS_WITHOUT_ATTENDEES"
}' http://localhost:8080/mail/registration-finished
