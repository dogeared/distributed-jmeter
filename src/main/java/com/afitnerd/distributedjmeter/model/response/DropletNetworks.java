package com.afitnerd.distributedjmeter.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class DropletNetworks {

    @JsonProperty("v4")
    List<V4Network> v4s;

    @JsonProperty("v6")
    List<V6Network> v6s;

    public List<V4Network> getV4s() {
        return v4s;
    }

    public void setV4s(List<V4Network> v4s) {
        this.v4s = v4s;
    }

    public List<V6Network> getV6s() {
        return v6s;
    }

    public void setV6s(List<V6Network> v6s) {
        this.v6s = v6s;
    }
}
