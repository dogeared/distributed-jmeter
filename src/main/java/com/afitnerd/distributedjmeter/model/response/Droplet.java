package com.afitnerd.distributedjmeter.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Droplet {

    long id;
    String name;
    long memory;
    int vcpus;
    int disk;
    boolean locked;
    String status;
    String kernel;

    @JsonProperty("created_at")
    String createdAt;

    List<String> features;

    @JsonProperty("backup_ids")
    List<String> backupIds;

    @JsonProperty("next_backup_window")
    String nextBackupWindow;

    @JsonProperty("snapshot_ids")
    List<String> snapshotIds;

    @JsonProperty("image")
    DropletImage dropletImage;

    @JsonProperty("volume_ids")
    List<String> volumeIds;

    @JsonProperty("size")
    DropletSize dropletSize;

    @JsonProperty("size_slug")
    String sizeSlug;

    @JsonProperty("networks")
    DropletNetworks dropletNetworks;

    @JsonProperty("region")
    DropletRegion dropletRegion;

    List<String> tags;

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

    public int getDisk() {
        return disk;
    }

    public void setDisk(int disk) {
        this.disk = disk;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getKernel() {
        return kernel;
    }

    public void setKernel(String kernel) {
        this.kernel = kernel;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }

    public List<String> getBackupIds() {
        return backupIds;
    }

    public void setBackupIds(List<String> backupIds) {
        this.backupIds = backupIds;
    }

    public String getNextBackupWindow() {
        return nextBackupWindow;
    }

    public void setNextBackupWindow(String nextBackupWindow) {
        this.nextBackupWindow = nextBackupWindow;
    }

    public List<String> getSnapshotIds() {
        return snapshotIds;
    }

    public void setSnapshotIds(List<String> snapshotIds) {
        this.snapshotIds = snapshotIds;
    }

    public DropletImage getDropletImage() {
        return dropletImage;
    }

    public void setDropletImage(DropletImage dropletImage) {
        this.dropletImage = dropletImage;
    }

    public List<String> getVolumeIds() {
        return volumeIds;
    }

    public void setVolumeIds(List<String> volumeIds) {
        this.volumeIds = volumeIds;
    }

    public DropletSize getDropletSize() {
        return dropletSize;
    }

    public void setDropletSize(DropletSize dropletSize) {
        this.dropletSize = dropletSize;
    }

    public String getSizeSlug() {
        return sizeSlug;
    }

    public void setSizeSlug(String sizeSlug) {
        this.sizeSlug = sizeSlug;
    }

    public DropletNetworks getDropletNetworks() {
        return dropletNetworks;
    }

    public void setDropletNetworks(DropletNetworks dropletNetworks) {
        this.dropletNetworks = dropletNetworks;
    }

    public DropletRegion getDropletRegion() {
        return dropletRegion;
    }

    public void setDropletRegion(DropletRegion dropletRegion) {
        this.dropletRegion = dropletRegion;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
