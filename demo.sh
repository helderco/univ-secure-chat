#!/bin/bash

# keytool -genkey -keyalg RSA -alias root -keystore root.jks -storepass qx4zE7na -validity 360

storepass="qx4zE7na"
keypass=$storepass

echo
echo "Generating two clients and a server for a demo."
echo "-----------------------------------------------"
echo

rm alice* bob* server*

# -----------------------

# Generate client certificate
echo "-> Creating Alice's keystore..."
keytool -genkey -keyalg RSA -alias alice -validity 360 \
          -keystore alice.jks -storepass $storepass \
          -keypass $keypass \
          -dname "CN=Alice Demo, O=SGR"

# Generate client certificate
echo "-> Creating Bobs's keystore..."
keytool -genkey -keyalg RSA -alias bob -validity 360 \
          -keystore bob.jks -storepass $storepass \
          -keypass $keypass \
          -dname "CN=Bob Demo, O=SGR"

# Generate server certificate
echo "-> Creating the Server's keystore..."
keytool -genkey -keyalg RSA -alias server -validity 3600 \
          -keystore server.jks -storepass $storepass \
          -keypass $keypass \
          -dname "CN=Server Demo, O=SGR"

# -------------------

echo "-> Importing alice into server..."

keytool -export -keystore alice.jks -alias alice \
          -storepass $storepass -file alice.cert

keytool -import -keystore server.jks -alias alice \
          -storepass $storepass -file alice.cert

# --------------------

echo "-> Importing bob into server..."

keytool -export -keystore bob.jks -alias bob \
          -storepass $storepass -file bob.cert

keytool -import -keystore server.jks -alias bob \
          -storepass $storepass -file bob.cert

# --------------------

echo "-> Importing server into clients..."

keytool -export -keystore server.jks -alias server \
          -storepass $storepass -file server.cert

keytool -import -keystore alice.jks -alias server \
          -storepass $storepass -file server.cert

keytool -import -keystore bob.jks -alias server \
          -storepass $storepass -file server.cert

# --------------------

echo "-> Importing alice into bob...."

keytool -import -keystore bob.jks -alias alice \
          -storepass $storepass -file alice.cert

echo "-> Importing bob into alice..."

keytool -import -keystore alice.jks -alias bob \
          -storepass $storepass -file bob.cert
