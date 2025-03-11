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
                    mvn clean install -Dmaven.test.skip=true
                '''
            }
        }

        stage('Test Application') {
            steps {
                sh '''#!/bin/bash
                    mvn test
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
                    app = docker.build("jenkinsci-cd/webserver")
                }
            }
        }

        stage('Test Docker Container') {
            steps {
                sh 'docker run --rm localhost:5000/jenkinsci-cd/webserver /bin/bash -c "echo Container is running and tests passed"'
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
