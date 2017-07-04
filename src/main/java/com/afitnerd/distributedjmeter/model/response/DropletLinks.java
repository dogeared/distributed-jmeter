package com.afitnerd.distributedjmeter.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class DropletLinks {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("pages")
    DropletPages dropletPages;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "actions")
    List<DropletAction> dropletActions;

    public DropletPages getDropletPages() {
        return dropletPages;
    }

    public void setDropletPages(DropletPages dropletPages) {
        this.dropletPages = dropletPages;
    }
}

class DropletPages {

    String first;

    @JsonProperty("prev")
    String previous;

    String next;
    String last;

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }
}

class DropletAction {

    String id;
    String href;
    String rel;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }
}
