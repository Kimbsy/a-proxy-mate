#!/bin/bash

pushd .

cd apm-server
./start.sh

cd ../apm-ui
./start.sh

popd
