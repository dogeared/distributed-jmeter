package com.afitnerd.distributedjmeter.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class V6Network {

    @JsonProperty("ip_address")
    String ipAddress;

    String netmask;
    String gateway;
    String type;

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getNetmask() {
        return netmask;
    }

    public void setNetmask(String netmask) {
        this.netmask = netmask;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
