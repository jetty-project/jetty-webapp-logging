# DO NOT EDIT - See: https://www.eclipse.org/jetty/documentation/current/startup-modules.html

  [description]Configure jetty server side to force all webappsto use server level logging libraries

  [tags]centralized-webapp-logging

  [ini]slf4j.version?=1.7.25logback.version?=1.2.3jetty-webapp-logging.version?=9.4-SNAPSHOT

  [lib]lib/logging/slf4j-api-${slf4j.version}.jarlib/logging/jcl-over-slf4j-${slf4j.version}.jarlib/logging/jul-to-slf4j-${slf4j.version}.jarlib/logging/log4j-over-slf4j-${slf4j.version}.jarlib/logging/logback-core-${logback.version}.jarlib/logging/logback-classic-${logback.version}.jarlib/logging/jetty-webapp-logging-${jetty-webapp-logging.version}.jar

  [files]logs/maven://org.slf4j/slf4j-api/${slf4j.version}|lib/logging/slf4j-api-${slf4j.version}.jarmaven://org.slf4j/jcl-over-slf4j/${slf4j.version}|lib/logging/jcl-over-slf4j-${slf4j.version}.jarmaven://org.slf4j/jul-to-slf4j/${slf4j.version}|lib/logging/jul-to-slf4j-${slf4j.version}.jarmaven://org.slf4j/log4j-over-slf4j/${slf4j.version}|lib/logging/log4j-over-slf4j-${slf4j.version}.jarmaven://ch.qos.logback/logback-core/${logback.version}|lib/logging/logback-core-${logback.version}.jarmaven://ch.qos.logback/logback-classic/${logback.version}|lib/logging/logback-classic-${logback.version}.jarmaven://org.mortbay.jetty.extras/jetty-webapp-logging/${jetty-webapp-logging.version}|lib/logging/jetty-webapp-logging-${jetty-webapp-logging.version}.jar

  [depends]resourceswebappdeploy

  [xml]etc/jetty-jul-to-slf4j.xmletc/jetty-mdc-handler.xmletc/jetty-webapp-logging.xml


