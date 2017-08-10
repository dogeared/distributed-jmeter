package com.afitnerd.distributedjmeter.service;

import com.afitnerd.distributedjmeter.model.request.CreateDropletRequest;
import com.afitnerd.distributedjmeter.model.response.Droplet;
import com.afitnerd.distributedjmeter.model.response.DropletResponse;
import com.jcraft.jsch.JSchException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface DOAPIService {

    static final String DO_API_BASE_URL = "https://api.digitalocean.com/v2";
    static final String DO_DROPLET_ENDPOINT = "/droplets";
    static final String DO_FIREWALL_ENDPOINT = "/firewalls";

    DropletResponse listDroplets() throws IOException;
    DropletResponse listDroplets(String tagName) throws IOException;
    List<String> getDropletIps() throws IOException;
    List<String> getDropletIps(String tagName) throws IOException;
    List<String> getDropletIps(List<Droplet> droplets);
    List<?> getDropletsAttribute(String attribute) throws IOException;
    List<?> getDropletsAttribute(List<Droplet> droplets, String attribute);
    List<Map<String, ?>> getDropletsAttributes(List<String> attributes) throws IOException;
    DropletResponse createDroplets(CreateDropletRequest request) throws IOException;
    void addDropletsToFirewall(List<Object> dropletIds) throws IOException;
    void addDropletsToFirewallByTags(List<String> tags) throws IOException;
}
