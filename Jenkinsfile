pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        sh 'rm -rf builds/libs/'
        sh 'chmod x gradlew'
        sh './gradlew build --refresh-dependencies --stacktrace'
        archiveArtifacts(fingerprint: true, onlyIfSuccessful: true, artifacts: '**/build/libs/*.jar')
      }
    }
  }
}