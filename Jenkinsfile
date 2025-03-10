pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'jenkinsci-cd/webserver'
        DOCKER_REGISTRY = 'http://localhost:5000'
        DOCKER_CREDENTIALS = 'docker-credentials'
        JAVA_HOME = '/usr/lib/jvm/java-17-openjdk-amd64'
        PATH = "${JAVA_HOME}/bin:${PATH}"
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
                    mvn clean install -Dmaven.test.skip=true
                '''
            }
        }

        stage('Archive Artifact') {
            steps {
                archiveArtifacts artifacts: 'target/*.war'
            }
        }

        stage('Build image') {
            steps {
                script {
                    app = docker.build("${DOCKER_IMAGE}:${env.BUILD_NUMBER}")
                }
            }
        }

        stage('Push image') {
            steps {
                script {
                    docker.withRegistry("${DOCKER_REGISTRY}", "${DOCKER_CREDENTIALS}") {
                        app.push("${env.BUILD_NUMBER}")
                        app.push("latest")
                    }
                }
            }
        }

        stage('Docker Test') {
            steps {
                script {
                    sh "docker run --rm ${DOCKER_IMAGE}:${env.BUILD_NUMBER} /bin/bash -c 'curl -f http://localhost:8090 || exit 1'"
                }
            }
        }

        stage('Run Docker Container') {
            steps {
                sh "docker run -d -p 80:80 ${DOCKER_REGISTRY}/${DOCKER_IMAGE}:${env.BUILD_NUMBER} &"
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
