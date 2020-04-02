# DO NOT EDIT - See: https://www.eclipse.org/jetty/documentation/current/startup-modules.html

[description]
Configure jetty server side to force all webapps
to use server level logging libraries

[tags]
centralized-webapp-logging

[depends]
resources
webapp
deploy
jul-slf4j
jcl-slf4j
log4j-over-slf4j
logging-logback

[ini]
jetty-webapp-logging.version?=${project.version}

[lib]
lib/logging/jetty-webapp-logging-${jetty-webapp-logging.version}.jar

[files]
logs/
maven://org.eclipse.jetty/jetty-webapp-logging/${jetty-webapp-logging.version}|lib/logging/jetty-webapp-logging-${jetty-webapp-logging.version}.jar

[xml]
etc/jetty-jul-to-slf4j.xml
etc/jetty-mdc-handler.xml
etc/jetty-webapp-logging.xml


