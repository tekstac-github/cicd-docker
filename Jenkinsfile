pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "jenkinsci-cd/webserver"
        DOCKER_REGISTRY = "localhost:5000"
        DOCKER_CREDENTIALS = "docker-credentials"
    }

    stages {
        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh '''#!/bin/bash
                   mvn clean package
                '''
            }
        }

        stage('Build image') {
            steps {
                script {
                    sh 'docker build -t $DOCKER_IMAGE .'
                }
            }
        }

        stage('Test image') {
            steps {
                script {
                    sh '''#!/bin/bash
                        docker stop test-webserver || true
                        docker rm test-webserver || true
                        docker run -d --name test-webserver -p 80:8090 ${DOCKER_IMAGE}
                        sleep 10
                        docker stop test-webserver
                        docker rm test-webserver
                    '''
                }
            }
        }

        stage('Push image') {
            steps {
                script {
                    docker.withRegistry("https://${DOCKER_REGISTRY}", "${DOCKER_CREDENTIALS}") {
                        app.push("${env.BUILD_NUMBER}")
                        app.push("latest")
                    }
                }
            }
        }

        stage('Run Docker Container') {
            steps {
                sh '''#!/bin/bash
                    docker stop webserver || true
                    docker rm webserver || true
                    docker run -d --name webserver -p 80:8090 ${DOCKER_REGISTRY}/${DOCKER_IMAGE}
                '''
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
