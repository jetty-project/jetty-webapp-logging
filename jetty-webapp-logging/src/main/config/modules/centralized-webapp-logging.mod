# DO NOT EDIT - See: https://www.eclipse.org/jetty/documentation/current/startup-modules.html

[description]
Configure jetty server side to force all webapps
to use server level logging libraries

[tags]
centralized-webapp-logging

[ini]
slf4j.version?=1.7.25
logback.version?=1.2.3
jetty-webapp-logging.version?=9.4.x-SNAPSHOT

[lib]
lib/logging/slf4j-api-${slf4j.version}.jar
lib/logging/jcl-over-slf4j-${slf4j.version}.jar
lib/logging/jul-to-slf4j-${slf4j.version}.jar
lib/logging/log4j-over-slf4j-${slf4j.version}.jar
lib/logging/logback-core-${logback.version}.jar
lib/logging/logback-classic-${logback.version}.jar
lib/logging/jetty-webapp-logging-${jetty-webapp-logging.version}.jar

[files]
logs/
maven://org.slf4j/slf4j-api/${slf4j.version}|lib/logging/slf4j-api-${slf4j.version}.jar
maven://org.slf4j/jcl-over-slf4j/${slf4j.version}|lib/logging/jcl-over-slf4j-${slf4j.version}.jar
maven://org.slf4j/jul-to-slf4j/${slf4j.version}|lib/logging/jul-to-slf4j-${slf4j.version}.jar
maven://org.slf4j/log4j-over-slf4j/${slf4j.version}|lib/logging/log4j-over-slf4j-${slf4j.version}.jar
maven://ch.qos.logback/logback-core/${logback.version}|lib/logging/logback-core-${logback.version}.jar
maven://ch.qos.logback/logback-classic/${logback.version}|lib/logging/logback-classic-${logback.version}.jar
maven://org.eclipse.jetty/jetty-webapp-logging/${jetty-webapp-logging.version}|lib/logging/jetty-webapp-logging-${jetty-webapp-logging.version}.jar

[depends]
resources
webapp
deploy

[xml]
etc/jetty-jul-to-slf4j.xml
etc/jetty-mdc-handler.xml
etc/jetty-webapp-logging.xml


