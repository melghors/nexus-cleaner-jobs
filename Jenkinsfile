pipeline {
    agent any

    stages {
        stage('build') {
            steps {
                lastChanges()
                echo 'Hello World'
                sh 'env'
            }
        }

        stage('deploy') {
            steps {
                echo 'Hello World'
                sh 'env'
            }
        }

    }
}
