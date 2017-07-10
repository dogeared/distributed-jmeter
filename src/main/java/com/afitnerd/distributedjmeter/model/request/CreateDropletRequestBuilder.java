package com.afitnerd.distributedjmeter.model.request;

import java.util.List;

public class CreateDropletRequestBuilder {

    private CreateDropletRequest createDropletRequest;

    private CreateDropletRequestBuilder() {}

    public static CreateDropletRequestBuilder builder() {
        CreateDropletRequestBuilder builder = new CreateDropletRequestBuilder();
        builder.createDropletRequest = new CreateDropletRequest();
        return builder;
    }

    public CreateDropletRequestBuilder addName(String name) {
        createDropletRequest.getNames().add(name);
        return this;
    }

    public CreateDropletRequestBuilder names(List<String> names) {
        createDropletRequest.setNames(names);
        return this;
    }

    public CreateDropletRequestBuilder region(String region) {
        createDropletRequest.setRegion(region);
        return this;
    }

    public CreateDropletRequestBuilder size(String size) {
        createDropletRequest.setSize(size);
        return this;
    }

    public CreateDropletRequestBuilder image(Integer image) {
        createDropletRequest.setImage(image);
        return this;
    }

    public CreateDropletRequestBuilder addSshKey(String keyId) {
        createDropletRequest.getSshKeys().add(keyId);
        return this;
    }

    public CreateDropletRequestBuilder backups(boolean backups) {
        createDropletRequest.setBackups(backups);
        return this;
    }

    public CreateDropletRequestBuilder ipv6(boolean ipv6) {
        createDropletRequest.setBackups(ipv6);
        return this;
    }

    public CreateDropletRequestBuilder userData(String userData) {
        createDropletRequest.setUserData(userData);
        return this;
    }

    public CreateDropletRequestBuilder privateNetworking(boolean privateNetworking) {
        createDropletRequest.setPrivateNetworking(privateNetworking);
        return this;
    }

    public CreateDropletRequestBuilder monitoring(boolean monitoring) {
        createDropletRequest.setMonitoring(monitoring);
        return this;
    }

    public CreateDropletRequestBuilder addTag(String tag) {
        createDropletRequest.getTags().add(tag);
        return this;
    }

    public CreateDropletRequest build() {
        return createDropletRequest;
    }
}
