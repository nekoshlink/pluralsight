set -x

rm *.csr
rm *.key
rm *.p12
rm *.crt
rm *.srl
rm *.jks

# Create the CA certificate

openssl req -x509 -sha512 -days 3650 -newkey rsa:4096 -keyout rootCA.key -out rootCA.crt

# Create the server-side TLS certificate

openssl req -new -newkey rsa:4096 -keyout localhost.key -out localhost.csr

openssl x509 -sha512 -req -CA rootCA.crt -CAkey rootCA.key -in localhost.csr -out localhost.crt -days 365 -CAcreateserial

openssl pkcs12 -export -out localhost.p12 -name "localhost" -inkey localhost.key -in localhost.crt

# Import to Java Key Store for Spring Boot application

keytool -importkeystore -srckeystore localhost.p12 -srcstoretype PKCS12 -destkeystore keystore.jks -deststoretype PKCS12
