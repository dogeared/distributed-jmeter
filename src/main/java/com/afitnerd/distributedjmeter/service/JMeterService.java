package com.afitnerd.distributedjmeter.service;

import com.afitnerd.distributedjmeter.model.response.DropletResponse;
import com.jcraft.jsch.JSchException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface JMeterService {

    String JMETER_SERVER_BASE = "jmeter-server";
    String JMETER_CLIENT = "jmeter-client";

    List<DropletResponse> createJMeterServerDroplets(int numDroplets, String size);
    Map<String, Object> checkJMeterServerDropletsActive(int numDroplets) throws IOException, JSchException;
    void addJMeterClientToFirewall() throws IOException;
    void addJMeterServersToFirewall() throws IOException;
    String jMeterClientStart(String remoteIps) throws IOException, JSchException;
    String jMeterServersStart() throws IOException, JSchException;
}
