set -x

rm client*.crt
rm client*.csr
rm client*.key
rm client*.p12

# Create the Java Trust Store for mTLS in the Spring Boot application

keytool -import -trustcacerts -noprompt -alias ca -ext san=dns:localhost,ip:127.0.0.1 -file rootCA.crt -keystore truststore.jks

# Create the client certificate for mTLS for "admin"

openssl req -new -newkey rsa:4096 -nodes -keyout clientAdmin.key -out clientAdmin.csr

openssl x509 -sha512 -req -CA rootCA.crt -CAkey rootCA.key -in clientAdmin.csr -out clientAdmin.crt -days 365 -CAcreateserial

openssl pkcs12 -export -out clientAdmin.p12 -name "clientAdmin" -inkey clientAdmin.key -in clientAdmin.crt

# Create the client certificate for mTLS for "user"

openssl req -new -newkey rsa:4096 -nodes -keyout clientUser.key -out clientUser.csr

openssl x509 -sha512 -req -CA rootCA.crt -CAkey rootCA.key -in clientUser.csr -out clientUser.crt -days 365 -CAcreateserial

openssl pkcs12 -export -out clientUser.p12 -name "clientUser" -inkey clientUser.key -in clientUser.crt

# Create the client certificate for mTLS for "guest"

openssl req -new -newkey rsa:4096 -nodes -keyout clientGuest.key -out clientGuest.csr

openssl x509 -sha512 -req -CA rootCA.crt -CAkey rootCA.key -in clientGuest.csr -out clientGuest.crt -days 365 -CAcreateserial

openssl pkcs12 -export -out clientGuest.p12 -name "clientGuest" -inkey clientGuest.key -in clientGuest.crt

