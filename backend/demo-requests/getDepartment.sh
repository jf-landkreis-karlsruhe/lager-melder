#!/bin/bash
curl -i -H "Authorization: Bearer $1" http://localhost:8080/departments/$2
