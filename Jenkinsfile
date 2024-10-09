pipeline {
    agent any

    environment {
        REACT_APP_VERSION = "1.0.$BUILD_ID"
        APP_NAME = 'kiryong-diet'
        AWS_DEFAULT_REGION = 'us-east-1'
        AWS_DOCKER_REGISTRY = '920373025050.dkr.ecr.us-east-1.amazonaws.com/kiryong-diet'
        AWS_ECS_CLUSTER = 'LearnJenkinsApp-Cluster-Prod'
        AWS_ECS_SERVICE_PROD ='LearnJenkinsApp-Service-Prod'
        AWS_ECS_TD_PROD = 'LearnJenkinsApp-TaskDefinition-Prod'
    }

    stages {
        stage('Build') {
            agent {
                docker {
                    image 'maven:3.8-jdk-17-slim'
                    reuseNode true
                }
            }
            steps {
                '''
                ls -la
                mvn clean install
                ls -la
                '''
            }
        }

         stage('Build Docker image') {
            agent {
                docker {
                    image 'my-aws-cli'
                    reuseNode true
                    args "-u root -v /var/run/docker.sock:/var/run/docker.sock --entrypoint=''"
                }
            }
            steps {
                withCredentials([usernamePassword(credentialsId: 'my-aws', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
                    sh '''
                        docker build -t $AWS_DOCKER_REGISTRY/$APP_NAME:$APP_VERSION .
                        aws ecr get-login-password | docker login --username AWS --password-stdin $AWS_DOCKER_REGISTRY
                        docker push $AWS_DOCKER_REGISTRY/$APP_NAME:$APP_VERSION
                    '''
                }
            }
        }

        stage('Deploy to AWS') {
    agent {
        docker {
            image 'my-aws-cli'
            reuseNode true
            args "--entrypoint=''"
        }
    }
    steps {
        withCredentials([usernamePassword(credentialsId: 'my-aws', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
            sh '''
                sed -i "s/#APP_VERSION#/$APP_VERSION/g" aws/task-definition-prod.json 
                LATEST_TD_REVISION=$(aws ecs register-task-definition --cli-input-json file://aws/task-definition-prod.json | jq '.taskDefinition.revision')  
                aws ecs update-service --cluster $AWS_ECS_CLUSTER --service $AWS_ECS_SERVICE_PROD --task-definition $AWS_ECS_TD_PROD:$LATEST_TD_REVISION 
                aws ecs wait services-stable --cluster $AWS_ECS_CLUSTER --services $AWS_ECS_SERVICE_PROD  
            '''
        } 
    }
}

    }
}