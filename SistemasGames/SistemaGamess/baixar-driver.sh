#!/bin/sh
set -eu
mkdir -p lib
curl -fL 'https://repo.maven.apache.org/maven2/com/mysql/mysql-connector-j/9.5.0/mysql-connector-j-9.5.0.jar' -o lib/mysql-connector-j-9.5.0.jar
echo 'Driver instalado em lib/mysql-connector-j-9.5.0.jar'
