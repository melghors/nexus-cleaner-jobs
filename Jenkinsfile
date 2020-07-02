
pipeline {
    agent any

    stages {
        stage('build') {
            when {
                changelog '.*'
            }
            steps {
                echo 'Hello World'
                // script {
                //     def changes = lastChanges(since: "LAST_SUCCESSFUL_BUILD", format: "LINE", matching: "LINE")
                // }
                // сurl --user admin:admin ${JOB_URL}lastSuccessfulBuild/api/json  | jq -r '.displayName' | cut -c2- > lastSuccessfulBuild
                sh """
                echo $BUILD_NUMBER > lastSuccessfulBuild
                cat lastSuccessfulBuild
                """                
                archiveArtifacts ('lastSuccessfulBuild')
                //publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'artifacts', reportFiles: 'lastSuccessfulBuild', reportName: 'lastSuccessfulBuild', reportTitles: ''])

            }
        }
        stage('deploy') {
            steps {
                // sh """
                // export LAST_SUCESSFUL_BUILD=`curl --user admin:admin ${JOB_URL}lastSuccessfulBuild/artifact/lastSuccessfulBuild`
                // """
                //http://localhost:8081/job/test-changes/job/master/lastSuccessfulBuild/artifact/lastSuccessfulBuild
                sh 'ls -lah'
                sh 'env'
                sh 'echo 1ol'
            }
        }

    }

    post {
        always {
            cleanWs(patterns: [[pattern: '*', type: 'INCLUDE'], [pattern: 'lastSuccessfulBuild', type: 'EXCLUDE']])
        }
    }
}
