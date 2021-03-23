import org.shadow.sdk.AzureSDK
import org.shadow.sdk.Configuration;

def call() {
    AzureSDK capCloud = AzureSDK.getInstance(this)
    Configuration yamlBuilder = Configuration.getInstance(this)

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
                        dev1ListOfIPs = capCloud.getIPs('dev1')
                        yamlBuilder.helloWorld()
                       /* yamlBuilder.configureValuesYAML('dev1','volpay-bo-perimeter.yaml',dev1ListOfIPs)
                        yamlBuilder.configureValuesYAML('dev1', 'volpay-bo-business.yaml',dev1ListOfIPs)
                        yamlBuilder.configureValuesYAML('dev1', 'volpay-rt-bg-business.yaml',dev1ListOfIPs)
                        yamlBuilder.configureValuesYAML('dev1', 'volpay-rt-cg-business.yaml',dev1ListOfIPs)
                        yamlBuilder.configureValuesYAML('dev1', 'volpay-rt-pr-business.yaml',dev1ListOfIPs)*/
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