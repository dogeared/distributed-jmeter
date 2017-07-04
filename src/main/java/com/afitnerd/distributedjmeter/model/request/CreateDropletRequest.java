package com.afitnerd.distributedjmeter.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CreateDropletRequest implements Serializable {

    List<String> names = new ArrayList<>();
    String region;
    String size;
    String image;

    @JsonProperty("ssh_keys")
    List<String> sshKeys = new ArrayList<>();

    boolean backups;
    boolean ipv6;

    @JsonProperty("user_data")
    String userData;

    @JsonProperty("private_networking")
    Boolean privateNetworking;

    boolean monitoring;
    List<String> tags = new ArrayList<>();

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getSshKeys() {
        return sshKeys;
    }

    public void setSshKeys(List<String> sshKeys) {
        this.sshKeys = sshKeys;
    }

    public boolean isBackups() {
        return backups;
    }

    public void setBackups(boolean backups) {
        this.backups = backups;
    }

    public boolean isIpv6() {
        return ipv6;
    }

    public void setIpv6(boolean ipv6) {
        this.ipv6 = ipv6;
    }

    public String getUserData() {
        return userData;
    }

    public void setUserData(String userData) {
        this.userData = userData;
    }

    public Boolean getPrivateNetworking() {
        return privateNetworking;
    }

    public void setPrivateNetworking(Boolean privateNetworking) {
        this.privateNetworking = privateNetworking;
    }

    public boolean isPrivateNetworking() {
        return privateNetworking;
    }

    public void setPrivateNetworking(boolean privateNetworking) {
        this.privateNetworking = privateNetworking;
    }

    public boolean isMonitoring() {
        return monitoring;
    }

    public void setMonitoring(boolean monitoring) {
        this.monitoring = monitoring;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
