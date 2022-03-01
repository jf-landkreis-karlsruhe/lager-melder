docker volume create caddy_data

docker run -d -p 80:80 -p 443:443 \
    -v $PWD/Caddyfile:/etc/caddy/Caddyfile \
    -v caddy_data:/data \
    caddy:2.4.6-alpine 


backend env variables
spring.datasource.username
spring.datasource.password
spring.mail.host
spring.mail.username
spring.mail.password
application.admin.passwordHash
SPRING_PROFILES_ACTIV=prod
