# Lagermelder

## Run database with docker

- `cd backend`
- `docker-compose -f docker-compose-mysql.yml up`

## Run backend

It is a spring boot application in kotlin with maven.

```
cd backend
./mvnw install
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
```

## Run frontend
```
cd frontend
npm install
VITE_BACKEND_URL=http://localhost:8080/api npm start
```

## Run app with database + backend + frontend

For the first time you might need to add rights: `chmod 577 ./run.sh` <br/>
Then just: `./run.sh`

## Build with Tag

- `git tag -a deploy -m"deploy" -f`
- `git push --tags --force`

## Example Data

See https://github.com/KordonDev/lager-melder/blob/main/backend/src/main/kotlin/de/kordondev/lagermelder/AttendeeApplication.kt

Event: <frontendUrl>/scanner/event001

## Backup
docker exec lm-database-prod sh -c 'exec mysqldump -ulager_melder_user -pPASSWORD --no-tablespaces lager_melder' > ./lm-database.sql


scp username@remote:/file/to/send /where/to/put

la@email.de
QO7zaZGwC2a0
