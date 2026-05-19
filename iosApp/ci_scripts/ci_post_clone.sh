#!/bin/sh
set -eu

if [ -d /opt/homebrew/opt/openjdk@17 ]; then
  echo "OpenJDK 17 already installed at /opt/homebrew/opt/openjdk@17"
else
  echo "Installing OpenJDK 17 via Homebrew..."
  brew install openjdk@17
fi

echo "JAVA_HOME=/opt/homebrew/opt/openjdk@17 must be set as a workflow environment variable in Xcode Cloud."
