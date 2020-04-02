# DO NOT EDIT - See: https://www.eclipse.org/jetty/documentation/current/startup-modules.html

[description]
Provides a Log4j bridge to SLF4J logging.

[tags]
logging
slf4j
internal

[depends]
slf4j-api
slf4j-impl

[provides]
log4j-api
log4j-impl
slf4j+log4j

[files]
maven://org.slf4j/log4j-over-slf4j/${slf4j.version}|lib/logging/log4j-over-slf4j-${slf4j.version}.jar

[lib]
lib/logging/log4j-over-slf4j-${slf4j.version}.jar

