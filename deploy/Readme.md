docker volume create caddy_data

docker run -d -p 80:80 -p 443:443 \
    -v $PWD/Caddyfile:/etc/caddy/Caddyfile \
    -v caddy_data:/data \
    caddy:2.4.6-alpine 
