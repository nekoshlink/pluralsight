The certificates in this folder all have password

`Passw0rd`

You can use them as they are, or create your own with the commands
described in this document.

You can also run the commands in the script

`create-server-certs.sh`

in a Linux machine.

Create the CA certificate
---

`openssl req -x509 -sha512 -days 3650 -newkey rsa:4096 -keyout rootCA.key -out rootCA.crt`

Create the server-side TLS certificate
---
1. create

`openssl req -new -newkey rsa:4096 -keyout localhost.key –out localhost.csr`

2. sign

`openssl x509 -sha512 -req -CA rootCA.crt -CAkey rootCA.key -in localhost.csr -out localhost.crt -days 365 -CAcreateserial`

3. export

`openssl pkcs12 -export -out localhost.p12 -name "localhost" -inkey localhost.key -in localhost.crt`

Configure the Spring Boot application for TLS
---

1. import to KeyStore so that Java can use for TLS

`keytool -importkeystore -srckeystore localhost.p12 -srcstoretype PKCS12 -destkeystore keystore.jks -deststoretype JKS`

2. configure Spring Boot for TLS

```properties
server.port=8443
server.ssl.enabled=true
server.ssl.key-store=certs/keystore.jks
server.ssl.key-store-password=${PASSWORD}
server.ssl.key-alias=localhost
server.ssl.key-password=${PASSWORD}
```

Configure the Spring Boot application for mTLS
---

1. create the trust store

`keytool -import -trustcacerts -noprompt -alias ca -ext san=dns:localhost,ip:127.0.0.1 -file rootCA.crt -keystore truststore.jks`

2. configure Spring Boot

```properties
server.ssl.client-auth=want
server.ssl.trust-store=certs/truststore.jks
server.ssl.trust-store-password=${PASSWORD}
```

Create the client certificate for mTLS
---

1. create the client certificate

`openssl req -new -newkey rsa:4096 -nodes -keyout clientAdmin.key -out clientAdmin.csr`

2. sign the client certificate

`openssl x509 -sha512 -req -CA rootCA.crt -CAkey rootCA.key -in clientAdmin.csr -out clientAdmin.crt -days 365 -CAcreateserial`

3. export

`openssl pkcs12 -export -out clientAdmin.p12 -name "clientAdmin" -inkey clientAdmin.key -in clientAdmin.crt`

4. repeat for all users with different Common Names (CN) for each

Configure HTTP clients
---

Add root CA certificate and client certificate to browsers and REST API GUIs

- PostMan

https://learning.postman.com/docs/sending-requests/certificates/

- Insomnia

https://docs.insomnia.rest/insomnia/client-certificates
