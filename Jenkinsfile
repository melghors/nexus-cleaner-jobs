pipeline {
    agent any

    stages {
        stage('build') {
            steps {
                lastChanges()
                lastChanges since: 'LAST_SUCCESSFUL_BUILD', format:'SIDE',matching: 'LINE'
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
