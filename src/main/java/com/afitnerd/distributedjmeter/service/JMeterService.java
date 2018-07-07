package com.afitnerd.distributedjmeter.service;

import com.afitnerd.distributedjmeter.model.response.DropletResponse;
import com.jcraft.jsch.JSchException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface JMeterService {

    String JMETER_SERVER_BASE = "jmeter-server";
    String JMETER_CLIENT = "jmeter-client";
    Float JVM_MEMORY = .60f;

    List<DropletResponse> createJMeterServerDroplets(int numDroplets, String size);
    DropletResponse createJMeterClientDroplet(String size);

    Map<String, Object> checkJMeterServerDropletsActive(int numDroplets) throws IOException, JSchException;
    Map<String, Object> checkJMeterClientDropletActive() throws IOException, JSchException;

    void addJMeterServersToFirewall() throws IOException;
    void addJMeterClientToFirewall() throws IOException;

    String jMeterServersStart() throws IOException, JSchException;
    void jMeterClientCopyTestPlan() throws IOException, JSchException;
    String jMeterClientStart(String remoteIps) throws IOException, JSchException;
}
