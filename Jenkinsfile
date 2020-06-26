def getBranchName() {
  return env.GIT_BRANCH
}

pipeline {
    agent any
    tools {
        maven 'Maven 3.6.0'
        jdk 'jdk8'
    }
    triggers {
        githubPush()
    }
    parameters {
        string(defaultValue: "/home/gramos/.m2/repository", description: 'm2 repo folder', name: 'M2_REPOSITORY')
    }
    stages {
        stage ('Initialize') {
            steps {
                sh '''
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                '''
            }
        }
        stage('Build') {
            steps {
                sh 'mvn clean install -DskipTests'
                echo 'Built successfully'
            }
        }
    }
}