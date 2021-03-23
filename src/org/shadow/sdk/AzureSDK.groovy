#!/usr/bin/env groovy
package org.shadow.sdk;

import net.sf.json.JSONArray

class AzureSDK implements Serializable {

    private static Script steps

    private static AzureSDK instance

    AzureSDK(steps) { this.steps = steps }

    JSONArray getIPs(String env){
        def resourceGroupName = env + '-volpay-occ-rg'
        def ipList = steps.sh script: 'az vm list-ip-addresses -g '+ resourceGroupName +' --query "[].virtualMachine.{ ip: network.privateIpAddresses[0], hostnames: [name]}"', returnStdout: true
        def ipListJSON = steps.readJSON text: ipList
        return ipListJSON
    }

    static AzureSDK getInstance(Script steps) {
        if (instance == null) {
            instance = new AzureSDK(steps)
        }
        return  instance
    }

}