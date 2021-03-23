import org.shadow.sdk.AzureSDK
import org.shadow.sdk.Configuration
import net.sf.json.JSONArray

def getComponentIPs(def valuesYAML, String component, JSONArray componentIPs) {
    List<String> cassandraIPs = new ArrayList<>()
    List<String> mongoIPs = new ArrayList<>()
    List<String> rabbitmqIPs = new ArrayList<>()
    //*List<String> zookeeperIPs = new ArrayList<>()*//*
    List<String> kafkaIPs = new ArrayList<>()

    for (ips in componentIPs) {
        def hostname = ips.hostnames[0]
        if (hostname.contains('cassandra')) {
            valuesYAML.hostAliases.add(ips)
            tempIp = ips.ip + ":9042"
            cassandraIPs << ips.ip
        } else if (hostname.contains('mongo')) {
            valuesYAML.hostAliases.add(ips)
            tempIp = ips.ip + ":27017"
            mongoIPs << ips.ip
        } else if (hostname.contains('rabbit')) {
            valuesYAML.hostAliases.add(ips)
            tempIp = ips.ip + ":5672"
            rabbitmqIPs << ips.ip
        } else if (hostname.contains('kafka')) {
            valuesYAML.hostAliases.add(ips)
            tempIp = ips.ip + ":9093"
            kafkaIPs << ips.ip
        }
    }
}

def configureValuesYAML(String environment, def file, JSONArray listOfIPs) {
    try {

        def valuesYAML = readYaml text: file
        println valuesYAML.toString()

        steps.sh 'echo hello'

        List<String> cassandraIPs = new ArrayList<>()
        List<String> mongoIPs = new ArrayList<>()
        List<String> rabbitmqIPs = new ArrayList<>()
        //*List<String> zookeeperIPs = new ArrayList<>()*//*
        List<String> kafkaIPs = new ArrayList<>()

        def tempIp

        valuesYAML.hostAliases.pop()

        cassandraIPs = getComponentIPs(valuesYAML, 'cassandra', listOfIPs)
        mongoIPs = getComponentIPs(valuesYAML,'mongod', listOfIPs)
        rabbitmqIPs = getComponentIPs(valuesYAML,'rabbit', listOfIPs)
        kafkaIPs = getComponentIPs(valuesYAML, 'kafka', listOfIPs)

        valuesYAML.host.cassandra = cassandraIPs.join(",")
        valuesYAML.host.mongodb = mongoIPs.join(",")

        // Currently Volpay supports only one zookeeper
        valuesYAML.host.zookeeper = kafkaIPs[0].replace("9093", "2181")
        valuesYAML.host.kafka = kafkaIPs.join(",")
        valuesYAML.host.rabbitmq = rabbitmqIPs.join(",")

        valuesYAML.database.cassandra = "earthport_write"
        valuesYAML.database.mongodb = "earthport_read"

        valuesYAML.credentials.rabbitmq_user = "rabbit"
        valuesYAML.credentials.rabbitmq_password = "rabbit"

        valuesYAML.credentials.mongo_user = "volpay"
        valuesYAML.credentials.mongo_password = "volpay"

        println(valuesYAML)

        writeYaml file: environment + '.yaml', data: valuesYAML
    }
    catch (Exception e) {
        println("Exception: ${e}")
    }

}

def call() {
    AzureSDK capCloud = AzureSDK.getInstance(this)
    //Configuration yamlBuilder = Configuration.getInstance(this)

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
                        def volpayBoBusiness = libraryResource "org/visa/jenkins/volpay-bo-business.yaml"
                        configureValuesYAML('dev1', volpayBoBusiness, dev1ListOfIPs)
                        /*yamlBuilder.configureValuesYAML('dev1', 'volpay-bo-business.yaml',dev1ListOfIPs)
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