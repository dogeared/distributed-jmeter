package com.afitnerd.distributedjmeter.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class DropletImage {

    long id;
    String name;
    String distribution;
    String slug;

    @JsonProperty("public")
    boolean publicAccess;

    List<String> regions;

    @JsonProperty("created_at")
    String createdAt;

    @JsonProperty("min_disk_size")
    int minDiskSize;

    String type;

    @JsonProperty("size_gigabytes")
    float sizeGigabytes;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistribution() {
        return distribution;
    }

    public void setDistribution(String distribution) {
        this.distribution = distribution;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public boolean isPublicAccess() {
        return publicAccess;
    }

    public void setPublicAccess(boolean publicAccess) {
        this.publicAccess = publicAccess;
    }

    public List<String> getRegions() {
        return regions;
    }

    public void setRegions(List<String> regions) {
        this.regions = regions;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getMinDiskSize() {
        return minDiskSize;
    }

    public void setMinDiskSize(int minDiskSize) {
        this.minDiskSize = minDiskSize;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getSizeGigabytes() {
        return sizeGigabytes;
    }

    public void setSizeGigabytes(float sizeGigabytes) {
        this.sizeGigabytes = sizeGigabytes;
    }
}
