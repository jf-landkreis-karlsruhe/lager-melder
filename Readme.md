# Events

## Install Attendee

It is a spring boot application in kotlin with maven.

```
cd attendee
./mvnw install
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
```

## Sart frontend
```
cd frontend
npm install
VITE_BACKEND_URL=http://localhost:8080/api npm start
```

## Customization

- in the backend in `resource/static/kreiszeltlager-logo.jpg` paste the current logo for the email header
- For the header past the new image to `src/assets/logo.jpg` and fix styles in `Header.vue`.

## Run backend with docker

- `cd attendee`
- `./docker-compose/docker-registry.sh`
- update `to` part of jib configuration into `<to>http://localhost:5000</to>` and allow unsecure connections
- `mvn compile jib:build`
- `cd docker-compose && docker-compose up && cd ..`
- `docker run -p8080:8080 localhost:5000/lager-melder:latest`

- `docker container stop registry`

## Run database with docker

- `cd attendee`
- `docker-compose -f docker-compose-mysql.yml up`

## Run app with database + backend + frontend

For the first time you might need to add rights: `chmod 577 ./run.sh` <br/>
Then just: `./run.sh`

## Build with Tag

- `git tag -a deploy -m"deploy" -f`
- `git push --tags --force`

## Example Data

See https://github.com/KordonDev/lager-melder/blob/main/attendee/src/main/kotlin/de/kordondev/attendee/AttendeeApplication.kt

Event: <frontendUrl>/scanner/event001
