#!/bin/bash

if test \! -x ./generate.sh
then
  echo "must be run from the resources directory"
  exit 1
fi
if test -z "$(command -v openssl)"
then
  echo "Need openssl."
  exit 1
fi

PASS="foobar"
VALIDITY_DAYS="3650"

echo "Generating root ca."
openssl req -new -x509 -days "$VALIDITY_DAYS" -keyout ca.key -out ca.pem -subj "/C=NO/CN=CA" -passout "pass:$PASS"

echo "Generating ca-signed certificate and key."
CERT=signed-cert
cat > conf.conf <<EOT
[req]
distinguished_name = req_distinguished_name
req_extensions = v3_req
prompt = no
[req_distinguished_name]
CN = localhost
[v3_req]
keyUsage = digitalSignature, keyEncipherment
extendedKeyUsage = serverAuth
subjectAltName = @alt_names
[alt_names]
DNS.1 = localhost
IP.1 = 127.0.0.1
EOT
openssl genrsa -out "$CERT.key" 2048
openssl req -new -config conf.conf -key "$CERT.key" -out "$CERT.csr"
openssl x509 -req -CA ca.pem -CAkey ca.key -in "$CERT.csr" -out "$CERT.pem" -extensions v3_req -extfile ./conf.conf -days "$VALIDITY_DAYS" -CAcreateserial -passin "pass:$PASS"

echo "Generating keystore"
openssl pkcs12 -export -in "$CERT.pem" -inkey "$CERT.key" -out keystore.p12 -name "$CERT" -passout "pass:$PASS"

rm -- *.key *.csr *.srl conf.conf
