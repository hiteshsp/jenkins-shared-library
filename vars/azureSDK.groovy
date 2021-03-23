import org.shadow.sdk.AzureSDK;

def call() {
    AzureSDK object = AzureSDK.getInstance(this)

    pipeline {
        agent {
            label 'docker'
        }
        options {
            timestamps()
            buildDiscarder(logRotator(numToKeepStr: '10'))
        }
        stages {
            stage('Test SDK') {
                steps {
                    script {
                        println object.toString()
                        println object.getIPs('dev1')
                    }
                }
            }
        }
        post {
            always {
                //cleanWs()
                echo "BUILD Completed"
            }
        }

    }
}