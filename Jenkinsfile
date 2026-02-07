pipeline {
  agent any

  options {
    timestamps()
    ansiColor('xterm')
    timeout(time: 30, unit: 'MINUTES')
    disableConcurrentBuilds()
    buildDiscarder(logRotator(numToKeepStr: '20', artifactNumToKeepStr: '20'))
    skipDefaultCheckout(true)
  }

  environment {
    SONARQUBE_SERVER = 'sonarqube'
    SONAR_TOKEN = credentials('sonar-jenkins-token')
 }

  stages {

    stage('Print OS & Runtime Info') {
      steps {
        sh '''
          set -eux
          uname -a || true
          cat /etc/os-release || true
          java -version || true
          git --version || true
          docker --version || true
        '''
      }
    }

    stage('Checkout') {
      steps {
        checkout scm
        sh '''
          set -eux
          chmod +x gradlew
          sed -i 's/\r$//' gradlew || true
          ./gradlew -v
        '''
      }
    }

  }
}
