#!/bin/bash

export TERM=${TERM:-dumb}
export BUILD_NUMBER=$(cat ../version/number)
export ROOT_FOLDER=$(pwd)
export GRADLE_USER_HOME="${ROOT_FOLDER}/.gradle"

./gradlew --scan --stacktrace --debug clean build publish
