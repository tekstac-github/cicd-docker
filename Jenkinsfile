pipeline {
    agent any

    stages {
        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh '''#!/bin/bash
                    /opt/maven/bin/mvn clean install -Dmaven.test.skip=true
                '''
            }
        }

        stage('Archive Artifact') {
            steps {
                archiveArtifacts artifacts: 'target/*.war'
            }
        }

        stage('Run Docker Container for Testing') {
            steps {
                sh 'docker run -d --name test-container -p 8080:8080 jenkinsci-cd/webserver'
            }
        }

        stage('Test Docker Container') {
            steps {
                sh 'sleep 10'
                sh 'curl -f http://localhost:8080 || exit 1'
                sh 'docker stop test-container'
                sh 'docker rm test-container'
            }
        }

        stage('Build image') {
            steps {
                script {
                    app = docker.build("jenkinsci-cd/webserver")
                }
            }
        }

        stage('Push image') {
            steps {
                script {
                    docker.withRegistry('https://localhost:5000', 'docker-credentials') {
                        app.push("${env.BUILD_NUMBER}")
                        app.push("latest")
                    }
                }
            }
        }

        stage('Run Docker Container') {
            steps {
                sh 'docker run -d -p 80:80 localhost:5000/jenkinsci-cd/webserver &'
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed.'
        }
    }
}
