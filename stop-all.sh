#!/bin/bash

pushd .

cd apm-server
./stop.sh

cd ../apm-ui
./stop.sh

popd
