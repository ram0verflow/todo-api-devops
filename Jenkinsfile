pipeline {
    agent any

    // CI trigger: Jenkins polls GitHub every minute and builds on any new commit
    triggers {
        pollSCM('* * * * *')
    }

    environment {
        // Homebrew paths on macOS so the Jenkins agent can find java
        PATH = "/opt/homebrew/opt/openjdk@17/bin:/opt/homebrew/bin:/usr/local/bin:${env.PATH}"
        // Enhancement: every artifact is versioned with the Jenkins build number
        APP_VERSION = "1.0.${env.BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/ram0verflow/todo-api-devops.git'
                sh 'git log -1 --oneline'
            }
        }

        // Enhancement: unit tests run before the build
        stage('Test') {
            steps {
                sh './mvnw -B test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Build') {
            steps {
                sh './mvnw -B -DskipTests -Drevision=${APP_VERSION} clean package'
            }
        }
    }

    // Post-build action: archive the versioned JAR
    post {
        success {
            archiveArtifacts artifacts: 'target/todo-api-*.jar', fingerprint: true
            echo "Built todo-api-${APP_VERSION}.jar"
        }
    }
}
