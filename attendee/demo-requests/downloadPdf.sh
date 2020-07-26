#!/bin/bash
curl -i -H "Authorization: Bearer $1" http://localhost:8080/registrationFiles/$2/$3 >> downloaded.pdf
echo "File saved to downloaded.pdf"
