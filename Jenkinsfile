pipeline {
    agent any
    environment {
        // Docker Hub Repository's name
        DOCKER_IMAGE = 'zfffan/teedy'          // your Docker Hub user name and Repository's name
        DOCKER_TAG   = "${env.BUILD_NUMBER}"   // use build number as tag
    }
    stages {
        stage('Build') {
            steps {
                checkout scmGit(
                    branches: [[name: '*/master']],
                    extensions: [],
                    userRemoteConfigs: [[url: 'https://github.com/13250547987/Teedy.git']]
                )
                sh 'mvn -B -DskipTests clean package'
            }
        }
        stage('Building image') {
            steps {
                script {
                    docker.build("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}")
                }
            }
        }
        stage('Upload image') {
            steps {
                script {
                    // 解包 Username/Password 凭据
                    withCredentials([usernamePassword(
                        credentialsId: 'dockerhub_credentials',
                        usernameVariable: 'DOCKERHUB_USR',
                        passwordVariable: 'DOCKERHUB_PSW'
                    )]) {
                        // 登录 Docker Hub
                        sh "docker login -u $DOCKERHUB_USR -p $DOCKERHUB_PSW"

                        // 推送带构建号的镜像
                        sh "docker push ${env.DOCKER_IMAGE}:${env.DOCKER_TAG}"

                        // 打上 latest 标签并推送
                        sh "docker tag ${env.DOCKER_IMAGE}:${env.DOCKER_TAG} ${env.DOCKER_IMAGE}:latest"
                        sh "docker push ${env.DOCKER_IMAGE}:latest"
                    }
                }
            }
        }
        stage('Run containers') {
            steps {
                script {
                    sh 'docker stop teedy-container-8081 || true'
                    sh 'docker rm teedy-container-8081 || true'

                    docker.image("${env.DOCKER_IMAGE}:${env.DOCKER_TAG}").run(
                        '--name teedy-container-8081 -d -p 8081:8080'
                    )

                    sh 'docker ps --filter "name=teedy-container"'
                }
            }
        }
    }
}
