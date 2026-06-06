pipeline {
    agent any
    
    environment {
        DOCKER_REGISTRY = 'docker.io'
        DOCKER_CREDENTIALS_ID = 'dockerhub-credentials'
        GITHUB_CREDENTIALS_ID = 'github-credentials'
    }
    
    stages {
        stage('Checkout') {
            steps {
                git branch: 'master',
                    credentialsId: "${GITHUB_CREDENTIALS_ID}",
                    url: 'https://github.com/pratham20021/Rrevcart_P2.git'
            }
        }
        
        stage('Build Backend') {
            steps {
                dir('microservices') {
                    bat 'mvn clean package -DskipTests'
                }
            }
        }
        
        stage('Build Frontend') {
            steps {
                dir('revcart-frontend') {
                    bat 'npm install'
                    bat 'npm run build'
                }
            }
        }
        
        stage('Build Docker Images') {
            steps {
                script {
                    def services = [
                        'api-gateway', 'auth-service', 'user-service',
                        'product-service', 'cart-service', 'order-service',
                        'payment-service', 'notification-service', 'delivery-service',
                        'analytics-service', 'admin-service'
                    ]
                    
                    services.each { service ->
                        bat "docker build -t ${DOCKER_REGISTRY}/revcart-${service}:latest ./microservices/${service}"
                    }
                    
                    bat "docker build -t ${DOCKER_REGISTRY}/revcart-frontend:latest ./revcart-frontend"
                }
            }
        }
        
        stage('Push to Docker Hub') {
            steps {
                echo 'Docker images built successfully! Push to Docker Hub manually or configure credentials.'
            }
        }
        
        stage('Deploy') {
            steps {
                echo 'Docker images ready! Deploy using: docker-compose up -d'
            }
        }
    }
    
    post {
        success {
            echo 'Pipeline executed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
        always {
            cleanWs()
        }
    }
}
