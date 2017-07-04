package com.afitnerd.distributedjmeter.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class DropletSize {

    String slug;
    long memory;
    int vcpus;
    long disk;
    float transfer;

    @JsonProperty("price_monthly")
    float priceMonthly;

    @JsonProperty("price_hourly")
    float priceHourly;

    List<String> regions;

    boolean available;

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public long getMemory() {
        return memory;
    }

    public void setMemory(long memory) {
        this.memory = memory;
    }

    public int getVcpus() {
        return vcpus;
    }

    public void setVcpus(int vcpus) {
        this.vcpus = vcpus;
    }

    public long getDisk() {
        return disk;
    }

    public void setDisk(long disk) {
        this.disk = disk;
    }

    public float getTransfer() {
        return transfer;
    }

    public void setTransfer(float transfer) {
        this.transfer = transfer;
    }

    public float getPriceMonthly() {
        return priceMonthly;
    }

    public void setPriceMonthly(float priceMonthly) {
        this.priceMonthly = priceMonthly;
    }

    public float getPriceHourly() {
        return priceHourly;
    }

    public void setPriceHourly(float priceHourly) {
        this.priceHourly = priceHourly;
    }

    public List<String> getRegions() {
        return regions;
    }

    public void setRegions(List<String> regions) {
        this.regions = regions;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
