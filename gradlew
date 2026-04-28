#!/bin/sh
APP_HOME="$(cd "$(dirname "$0")" && pwd -P)"

if [ -n "$JAVA_HOME" ] ; then
    JAVACMD="$JAVA_HOME/bin/java"
else
    JAVACMD="java"
fi

exec "$JAVACMD" \
  -classpath "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" \
  "-Dorg.gradle.appname=Gradle" \
  org.gradle.wrapper.GradleWrapperMain \
  "$@"
