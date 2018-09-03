# Self-signed certificate

## Command to generate cert and private key

In order to run enable nginx proxy with TLS, we need to generate certificates.
In this case, we can use self-signed X509 certificates.

Below is what I used to generate certificate and the private key using OpenSSL in Windows.

```sh
openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout cert.key -out cert.crt
```

Remember: the self-signed certificates will be expired after X numbers of days. 

## Location
For my current setup, I put 2 files `cert.key` and `cert.crt` into this `nginx/cert` directory.
Also in `nginx/conf/nginx.conf`, I specify its location with respected to the `nginx.conf` file.

```nginx
ssl_certificate           ../cert/cert.crt;
ssl_certificate_key       ../cert/cert.key;
```

