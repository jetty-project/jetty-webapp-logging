#!groovy

pipeline {
  agent any
  // save some io during the build
  options { durabilityHint('PERFORMANCE_OPTIMIZED') }
  stages {
    stage("Parallel Stage") {
      parallel {
        stage("Build / Test - JDK8") {
          agent { node { label 'linux' } }
          steps {
            timeout(time: 120, unit: 'MINUTES') {
              mavenBuild("jdk8", "install", "maven3", true)
              warnings consoleParsers: [[parserName: 'Maven'], [parserName: 'Java']]
              junit testResults: '**/target/surefire-reports/*.xml,**/target/invoker-reports/TEST*.xml'
            }
          }
        }

        stage("Build / Test - JDK11") {
          agent { node { label 'linux' } }
          steps {
            timeout(time: 120, unit: 'MINUTES') {
              mavenBuild("jdk11", "install", "maven3", true)
              warnings consoleParsers: [[parserName: 'Maven'], [parserName: 'Java']]
              junit testResults: '**/target/surefire-reports/*.xml,**/target/invoker-reports/TEST*.xml'
            }
          }
        }

        stage("Build / Test - JDK13") {
          agent { node { label 'linux' } }
          steps {
            timeout(time: 120, unit: 'MINUTES') {
              mavenBuild("jdk13", "install", "maven3", true)
              warnings consoleParsers: [[parserName: 'Maven'], [parserName: 'Java']]
              junit testResults: '**/target/surefire-reports/*.xml,**/target/invoker-reports/TEST*.xml'
            }
          }
        }

        stage("Build Javadoc") {
          agent { node { label 'linux' } }
          steps {
            timeout(time: 30, unit: 'MINUTES') {
              mavenBuild("jdk11", "install javadoc:javadoc -DskipTests", "maven3", true)
              warnings consoleParsers: [[parserName: 'Maven'], [parserName: 'JavaDoc'], [parserName: 'Java']]
            }
          }
        }
      }
    }
  }
}

/**
 * To other developers, if you are using this method above, please use the following syntax.
 *
 * mavenBuild("<jdk>", "<profiles> <goals> <plugins> <properties>"
 *
 * @param jdk the jdk tool name (in jenkins) to use for this build
 * @param cmdline the command line in "<profiles> <goals> <properties>"`format.
 * @return the Jenkinsfile step representing a maven build
 */
def mavenBuild(jdk, cmdline, mvnName, junitPublishDisabled) {
  def localRepo = ".repository"
  def mavenOpts = '-Xms1g -Xmx4g -Djava.awt.headless=true'

  withMaven(
      maven: mvnName,
      jdk: "$jdk",
      publisherStrategy: 'EXPLICIT',
      options: [junitPublisher(disabled: junitPublishDisabled),mavenLinkerPublisher(disabled: false),pipelineGraphPublisher(disabled: false)],
      mavenOpts: mavenOpts,
      mavenLocalRepo: localRepo) {
    // Some common Maven command line + provided command line
    sh "mvn -Pci -V -B -T3 -e -Dmaven.test.failure.ignore=true -Djetty.testtracker.log=true $cmdline"
  }
}


// vim: et:ts=2:sw=2:ft=groovy
