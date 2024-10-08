pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh './gradlew clean build'
            }
        }
        stage('Test') {
            steps {
                sh './gradlew test'
            }
        }
        stage('Package') {
            steps {
                sh './gradlew bootJar'
            }
        }
        stage('Deploy') {
            steps {
                // Add your deployment steps here, e.g., using SCP, SSH, Docker, etc.
                sh 'scp build/libs/*.jar user@server:/path/to/deploy'
            }
        }
    }
    post {
        success {
            echo 'Build and Deploy succeeded!'
        }
        failure {
            echo 'Build or Deploy failed!'
        }
    }
}