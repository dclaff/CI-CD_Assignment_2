#!/usr/bin/env bash
set -euo pipefail
DIR="$(cd "$(dirname "$0")/.." && pwd)/.mvn/wrapper"
mkdir -p "$DIR"
JAR_URL="https://repo1.maven.org/maven2/io/takari/maven-wrapper/0.5.6/maven-wrapper-0.5.6.jar"
JAR_PATH="$DIR/maven-wrapper.jar"
echo "Downloading maven-wrapper.jar to $JAR_PATH"
if command -v curl >/dev/null 2>&1; then
  curl -fSL -o "$JAR_PATH" "$JAR_URL"
else
  wget -O "$JAR_PATH" "$JAR_URL"
fi
echo "Downloaded. You can now zip the project including .mvn/wrapper/maven-wrapper.jar"
