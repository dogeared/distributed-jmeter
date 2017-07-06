package com.afitnerd.distributedjmeter.service;

import com.afitnerd.distributedjmeter.model.request.CreateDropletRequest;
import com.afitnerd.distributedjmeter.model.response.Droplet;
import com.afitnerd.distributedjmeter.model.response.DropletResponse;
import com.jcraft.jsch.JSchException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface DOAPIService {

    DropletResponse listDroplets() throws IOException;
    DropletResponse listDroplets(String tagName) throws IOException;
    List<String> getDropletIps() throws IOException;
    List<String> getDropletIps(String tagName) throws IOException;
    List<String> getDropletIps(List<Droplet> droplets);
    List<Object> getDropletsAttribute(String attribute) throws IOException;
    List<Object> getDropletsAttribute(List<Droplet> droplets, String attribute);
    DropletResponse createDroplets(CreateDropletRequest request) throws IOException;
    List<Map<String, Object>> getDropletsAttributes(List<String> attributes) throws IOException;
    void addDropletsToFirewall(List<Object> dropletIds) throws IOException;
    void addDropletsToFirewallByTags(List<String> tags) throws IOException;
}
