#!/bin/bash

lein uberjar
java -jar target/uberjar/a-proxy-mate-0.1.0-SNAPSHOT-standalone.jar &
echo $! > a-proxy-mate.pid
