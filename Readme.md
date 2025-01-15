# Lagermelder

## Quickstart when app already run on your machine (docker images already created)

- Then start them with:
  ```
  ./run.sh
  ```
  (does `docker-compose -f ./backend/docker-compose/docker-compose.yml up`)
- For Frontend Development, start with custom backend url:
  ```
  npm run start
  ```
  (does `VITE_BACKEND_URL=http://127.0.0.1:8080/api vite --host 0.0.0.0 --port 9000`)

## Run database with docker

- `cd backend/docker-compose`
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

## Customization

- in the backend in `resource/static/kreiszeltlager-logo.jpg` paste the current logo for the email header
- For the header past the new image to `src/assets/logo.jpg` and fix styles in `Header.vue`.
- Update batch with new logo and text in media and export pdf to `backend/src/main/resource/data/batch.pdf`
