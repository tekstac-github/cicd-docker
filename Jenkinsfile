pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'jenkinsci-cd/webserver'
        DOCKER_REGISTRY = 'https://localhost:5000'
        DOCKER_CREDENTIALS = 'docker-credentials'
        TOMCAT_PATH = '/var/lib/tomcat9'
        WAR_FILE = 'taxiapp*.war'
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
                sh 'docker run -d --name test-container -p 8088:8088 $DOCKER_IMAGE'
            }
        }

        stage('Test Docker Container') {
            steps {
                sh 'sleep 10'
                sh 'curl -f http://localhost:8088 || exit 1'
                sh 'docker stop test-container'
                sh 'docker rm test-container'
            }
        }

        stage('Build image') {
            steps {
                script {
                    app = docker.build("$DOCKER_IMAGE")
                }
            }
        }

        stage('Push image') {
            steps {
                script {
                    docker.withRegistry("$DOCKER_REGISTRY", "$DOCKER_CREDENTIALS") {
                        app.push("${env.BUILD_NUMBER}")
                        app.push("latest")
                    }
                }
            }
        }

        stage('Run Docker Container') {
            steps {
                sh 'docker run -d -p 80:80 $DOCKER_REGISTRY/$DOCKER_IMAGE &'
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
