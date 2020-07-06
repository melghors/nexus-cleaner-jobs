
pipeline {
    agent any

    stages {
        stage('app build') {
            when {
                changelog '.*'
            }
            steps {
                echo 'Hello World'
                sh """
                  echo "deploy ${BUILD_NUMBER}"
                """
            }
        }
        stage('docker build') {
            when {
                changelog '.*'
            }
            steps {
                echo 'Hello World'
                sh """
                  echo "deploy ${BUILD_NUMBER}"
                """
            }
        }
        stage('deploy') {
            steps {
                script {
                    //env.TAG = sh(returnStdout: true, script: "curl --user zuser:vVk858dC https://nexus.zdevcode.com/repository/docker-internal/v2/processing/dev/tags/list -sb -H 'Accept: application/vnd.docker.distribution.manifest.v2+json' | jq '.tags[-1]' --raw-output")
                    env.MANIFEST_NAME = "processing/dev"
                    env.TAG = sh(returnStdout: true, script: "python3 1.py")
                }
                sh """
                  env
                  echo "deploy ${TAG}"
                """
            }
        }

    }
}
