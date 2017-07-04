package com.afitnerd.distributedjmeter.service;

import com.afitnerd.distributedjmeter.model.request.CreateDropletRequest;
import com.afitnerd.distributedjmeter.model.response.DropletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface DOAPIService {

    DropletResponse listDroplets() throws IOException;
    List<String> getDropletIps() throws IOException;
    List<Object> getDropletsAttribute(String attribute) throws IOException;
    DropletResponse createDroplets(CreateDropletRequest request) throws IOException;
    List<Map<String, Object>> getDropletsAttributes(List<String> attributes) throws IOException;
}
