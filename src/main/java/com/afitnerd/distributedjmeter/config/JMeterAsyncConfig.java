package com.afitnerd.distributedjmeter.config;

import com.afitnerd.distributedjmeter.model.response.DropletResponse;
import com.afitnerd.distributedjmeter.service.JMeterService;
import com.jcraft.jsch.JSchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

@Configuration
@EnableAsync
public class JMeterAsyncConfig {

    @Autowired
    JMeterService jMeterService;

    @Async
    public Future<List<DropletResponse>> createJMeterServerDroplets(int numDroplets, String size) {
        return new AsyncResult<>(jMeterService.createJMeterServerDroplets(numDroplets, size));
    }

    @Async
    public Future<DropletResponse> createJMeterClientDroplet(String size) {
        return new AsyncResult<>(jMeterService.createJMeterClientDroplet(size));
    }

    @Async
    public Future<Map<String, Object>> checkJMeterServerDropletsActive(int numDroplets) throws IOException, JSchException {
        Map<String, Object> result = null;
        while (true) {
            result = jMeterService.checkJMeterServerDropletsActive(numDroplets);
            if (result.get("all_jmeter") != null && (boolean) result.get("all_jmeter")) {
                break;
            }
        }
        return new AsyncResult<>(result);
    }

    @Async
    public Future<Map<String, Object>> checkJMeterClientDropletActive() throws IOException, JSchException {
        Map<String, Object> result = null;
        while (true) {
            result = jMeterService.checkJMeterClientDropletActive();
            if (result.get("all_jmeter") != null && (boolean) result.get("all_jmeter")) {
                break;
            }
        }
        return new AsyncResult<>(result);
    }

    @Async
    public Future<String> addJMeterServersToFirewall() throws IOException {
        jMeterService.addJMeterServersToFirewall();
        return new AsyncResult<>("added jmeter servers to firewall");
    }

    @Async
    public Future<String> addJMeterClientToFirewall() throws IOException {
        jMeterService.addJMeterClientToFirewall();
        return new AsyncResult<>("added jmeter client to firewall");
    }

    @Async
    public Future<String> jMeterServersStart() throws IOException, JSchException {
        return new AsyncResult<>(jMeterService.jMeterServersStart());
    }

    @Async
    public Future<String> jMeterClientCopyTestPlan() throws IOException, JSchException {
        jMeterService.jMeterClientCopyTestPlan();
        return new AsyncResult<>("copied over test plan for JMeter Client");
    }

    @Async
    public Future<String> jMeterClientStart(String remoteIps) throws IOException, JSchException {
        return new AsyncResult<>(jMeterService.jMeterClientStart(remoteIps));
    }
}
