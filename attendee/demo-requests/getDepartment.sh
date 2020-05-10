#!/bin/bash
curl -i -H "Authorization: Bearer $1" http://localhost:8080/department/$2
