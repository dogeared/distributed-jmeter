package com.afitnerd.distributedjmeter.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class DropletResponse {

    List<Droplet> droplets;

    @JsonProperty("links")
    DropletLinks dropletLinks;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("meta")
    DropletMeta dropletMeta;

    public List<Droplet> getDroplets() {
        return droplets;
    }

    public void setDroplets(List<Droplet> droplets) {
        this.droplets = droplets;
    }

    public DropletLinks getDropletLinks() {
        return dropletLinks;
    }

    public void setDropletLinks(DropletLinks dropletLinks) {
        this.dropletLinks = dropletLinks;
    }

    public DropletMeta getDropletMeta() {
        return dropletMeta;
    }

    public void setDropletMeta(DropletMeta dropletMeta) {
        this.dropletMeta = dropletMeta;
    }
}

class DropletMeta {

    long total;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
