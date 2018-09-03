# play-scala-nginx

## Configure nginx

First, follow steps listed in `cert/README.md` to generate self-signed certificate and private keys.

Next, download `nginx` and place `cert` in its root directory, also replace `nginx.conf` found in the `conf` directory.

To start and stop nginx, use commands below.
```sh
# start nginx
nginx

# stop nginx
nginx -s stop
```

Note that, in `nginx.conf`, we configure `proxy_pass` from HTTPS port `9005` to HTTP `9000` 
for both `/` for normal HTTP requests and `/ws` for websocket connection.

```nginx
server {
    listen 9005 ssl;
    server_name localhost;

    ssl_certificate           ../cert/cert.crt;
    ssl_certificate_key       ../cert/cert.key;

    ssl_session_cache  builtin:1000  shared:SSL:10m;
    ssl_protocols  TLSv1 TLSv1.1 TLSv1.2;
    ssl_ciphers HIGH:!aNULL:!eNULL:!EXPORT:!CAMELLIA:!DES:!MD5:!PSK:!RC4;
    ssl_prefer_server_ciphers on;

    # access_log            logs/https.access.log;

    location / {
        proxy_pass          http://localhost:9000;
        # proxy_read_timeout  90;
        proxy_redirect      off;
    }

    location /ws {
        proxy_pass          http://localhost:9000;
        proxy_http_version  1.1;
        proxy_set_header    Upgrade $http_upgrade;
        proxy_set_header    Connection "upgrade";
        proxy_read_timeout  86400;
    }
}
```

## Configure and run http/websocket app

```bash
sbt ~run
```

Then, the backend will be available at 2 URLs:
```bash
# http
http://localhost:9000

# https proxied by nginx
https://localhost:9005
```

There is a script for the `index.html` which configures the websocket to connect to HTTPS port `9005`.

```js
var wsUrl = 'wss://localhost:9005/ws/orders/table/' + tableId;
ws = new WebSocket(wsUrl);
```

Note that, the HTTP port `9000` should work too. We can avoid exposing this HTTP port `9000` 
by randomizing this port number via startup script and set it to environment variable.
 With this, we can tweak the config files of Nginx and Play to make sure 
 Play app can expose to the random port and Nginx can proxy to it.



