#!/bin/bash

set -o nounset \
    -o errexit

printf "Deleting previous (if any)..."
rm -rf secrets
mkdir secrets
mkdir -p tmp
echo " OK!"
# Generate CA key
printf "Creating CA..."
openssl req -new -x509 -keyout secrets/luxoft-eas-026-ca.key -out secrets/luxoft-eas-026-ca.crt -days 365 -subj '/CN=luxoft/OU=eas-026/O=luxoft/L=eindhoven/C=nl' -passin pass:secret -passout pass:secret

echo " OK!"

for i in 'broker' 'producer' 'consumer' 'schema-registry' 'kafka-rest'
do
	printf "Creating cert and keystore of $i..."
	# Create keystores
	keytool -genkey -noprompt \
				 -alias $i \
				 -dname "CN=$i, OU=eas-026, O=luxoft, L=eindhoven, C=nl" \
				 -keystore secrets/$i.keystore.jks \
				 -keyalg RSA \
				 -storepass secret \
				 -keypass secret  >/dev/null 2>&1

	# Create CSR, sign the key and import back into keystore
	keytool -keystore secrets/$i.keystore.jks -alias $i -certreq -file tmp/$i.csr -storepass secret -keypass secret >/dev/null 2>&1

	openssl x509 -req -CA secrets/luxoft-eas-026-ca.crt -CAkey secrets/luxoft-eas-026-ca.key -in tmp/$i.csr -out tmp/$i-ca-signed.crt -days 365 -CAcreateserial -passin pass:secret  >/dev/null 2>&1

	keytool -keystore secrets/$i.keystore.jks -alias CARoot -import -noprompt -file secrets/luxoft-eas-026-ca.crt -storepass secret -keypass secret >/dev/null 2>&1

	keytool -keystore secrets/$i.keystore.jks -alias $i -import -file tmp/$i-ca-signed.crt -storepass secret -keypass secret >/dev/null 2>&1

	# Create truststore and import the CA cert.
	keytool -keystore secrets/$i.truststore.jks -alias CARoot -import -noprompt -file secrets/luxoft-eas-026-ca.crt -storepass secret -keypass secret >/dev/null 2>&1
	echo "DONE!"
done

echo "secret" > secrets/cert_creds
rm -rf tmp

echo "SUCCEEDED"
