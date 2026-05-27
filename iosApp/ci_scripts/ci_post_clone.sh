#!/bin/sh
set -eu

BREW_PREFIX=$(brew --prefix)
JDK_PATH="$BREW_PREFIX/opt/openjdk@17"

if [ ! -d "$JDK_PATH" ]; then
  echo "Installing OpenJDK 17 via Homebrew..."
  brew install openjdk@17
fi

JAVA_HOME_PATH="$JDK_PATH/libexec/openjdk.jdk/Contents/Home"

mkdir -p "$HOME/.gradle"
echo "org.gradle.java.home=$JAVA_HOME_PATH" >> "$HOME/.gradle/gradle.properties"

echo "Configured Gradle to use JDK at $JAVA_HOME_PATH"
