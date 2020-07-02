
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
                // export LAST_SUCESSFUL_BUILD=`curl --user admin:admin ${JOB_URL}lastSuccessfulBuild/api/json  | jq -r '.displayName' | cut -c2-`
                // """
                //sh 'echo $LAST_SUCESSFUL_BUILD > lastSuccessfulBuild'
                script {
                  lastSuccessfulBuild = readFile('lastSuccessfulBuild')
                  env.LAST_SUCCESSFUL_BUILD = lastSuccessfulBuild
                  if (env.LAST_SUCCESSFUL_BUILD != BUILD_NUMBER) {
                    env.BUILD_NUMBER = env.LAST_SUCCESSFUL_BUILD
                  }
                }
                sh """
                  deploy ${BUILD_NUMBER}
                """
            }
        }

    }

    post {
        always {
            cleanWs(patterns: [[pattern: 'lastSuccessfulBuild', type: 'EXCLUDE'], [pattern: '*', type: 'INCLUDE']])
        }
    }
}
