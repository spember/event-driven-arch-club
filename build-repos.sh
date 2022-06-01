# !/bin/bash

cd chair-messages-lib && ./gradlew clean build pTML
pwd
cd ../admin && ./gradlew clean build jar
pwd
cd ../chairfront && ./gradlew clean build jar
pwd
cd ../chairhouse && ./gradlew clean build jar
pwd
