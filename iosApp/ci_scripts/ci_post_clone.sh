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

SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
REPO_ROOT=$(cd "$SCRIPT_DIR/../.." && pwd)
PROJECT_PROPS="$REPO_ROOT/gradle.properties"

if [ -f "$PROJECT_PROPS" ]; then
  /usr/bin/sed -i '' \
    -e 's|^org\.gradle\.jvmargs=.*|org.gradle.jvmargs=-Xmx6g -Dfile.encoding=UTF-8|' \
    -e 's|^kotlin\.daemon\.jvmargs=.*|kotlin.daemon.jvmargs=-Xmx6g|' \
    "$PROJECT_PROPS"
  echo "Bumped Gradle/Kotlin daemon heap for CI in $PROJECT_PROPS"
fi

echo "Configured Gradle to use JDK at $JAVA_HOME_PATH"
