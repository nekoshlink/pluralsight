set -x

rm clientNekoadm.crt
rm clientNekoadm.csr
rm clientNekoadm.key
rm clientNekoadm.p12

# Create the Java Trust Store for mTLS in the Spring Boot application

keytool -import -trustcacerts -noprompt -alias ca -ext san=dns:localhost,ip:127.0.0.1 -file rootCA.crt -keystore truststore.jks

# Create the client certificate for mTLS for "nekoadm"

openssl req -new -newkey rsa:4096 -nodes -keyout clientNekoadm.key -out clientNekoadm.csr

openssl x509 -sha512 -req -CA rootCA.crt -CAkey rootCA.key -in clientNekoadm.csr -out clientNekoadm.crt -days 365 -CAcreateserial

openssl pkcs12 -export -out clientNekoadm.p12 -name "clientNekoadm" -inkey clientNekoadm.key -in clientNekoadm.crt
