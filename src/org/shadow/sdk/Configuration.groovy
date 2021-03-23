#!/usr/bin/env groovy
package org.shadow.sdk

import net.sf.json.JSONArray

class Configuration implements Serializable {

    private static Script steps

    private static Configuration instance

    Configuration(steps) { this.steps = steps }

    static Configuration getInstance(Script steps) {
        if (instance == null) {
            instance = new Configuration(steps)
        }
        return  instance
    }

    /*def getComponentIPs(String component, JSONArray componentIPs) {
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
    }*/

    def helloWorld() {
        steps.sh script: 'echo helloworld', returnStdout: true
        def lib = steps.libraryResource "com/visa/jenkins/volpay-bo-business.yaml"
        println lib.toString()
    }

    def configureValuesYAML(String environment, String fileName, JSONArray listOfIPs) {
        try {
            def filePath = "com/visa/jenkins/volpay-bo-business.yaml"
            def valuesFile = steps.libraryResource filePath

            println(valuesFile)
            /*println valuesFile.toString()
            def valuesYAML = steps.readYaml text: valuesFile
            println valuesYAML.toString()

            steps.sh 'echo hello'

            List<String> cassandraIPs = new ArrayList<>()
            List<String> mongoIPs = new ArrayList<>()
            List<String> rabbitmqIPs = new ArrayList<>()
            *//*List<String> zookeeperIPs = new ArrayList<>()*//*
            List<String> kafkaIPs = new ArrayList<>()

            def tempIp

            valuesYAML.hostAliases.pop()

            cassandraIPs = getComponentIPs('cassandra', listOfIPs)
            mongoIPs = getComponentIPs('mongod', listOfIPs)
            rabbitmqIPs = getComponentIPs('rabbit', listOfIPs)
            kafkaIPs = getComponentIPs('kafka', listOfIPs)

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

            steps.writeYaml file: environment + '.yaml', data: valuesYAML*/
        }
        catch (Exception e) {
            println("Exception: ${e}")
        }

    }

    static String getRestEndpoint(env) { return "eps3-${env}.earthport.com" }

}