pipeline {
    agent any

    stages {
        stage('build') {
            steps {
                echo 'Hello World'
                script {
                    def changes = lastChanges(since: "LAST_SUCCESSFUL_BUILD", format: "LINE", matching: "LINE")
                    echo "$changes"
                }
                sh """
                curl ${JOB_URL}last-changes
                export LAST_SUCESSFUL_BUILD=`curl --user admin:admin ${JOB_URL}lastSuccessfulBuild/api/json  | jq -r '.displayName' | cut -c2-`
                """
                sh 'echo $LAST_SUCESSFUL_BUILD'
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
