package com.afitnerd.distributedjmeter.service;

import com.afitnerd.distributedjmeter.model.request.CreateDropletRequest;
import com.afitnerd.distributedjmeter.model.request.CreateDropletRequestBuilder;
import com.afitnerd.distributedjmeter.model.response.Droplet;
import com.afitnerd.distributedjmeter.model.response.DropletResponse;
import com.afitnerd.distributedjmeter.util.DODropletNameUtil;
import com.afitnerd.distributedjmeter.util.JMeterCommandUtil;
import com.jcraft.jsch.JSchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.afitnerd.distributedjmeter.util.JMeterCommandUtil.LOCAL_TEST_PLAN_NAME;

@Service
public class JMeterServiceImpl implements JMeterService {

    @Value("#{ @environment['jmeter.test.plan.file'] }")
    protected String jMeterTestPlanFile;

    @Value("#{ @environment['do.image.id'] ?: 26136050 }") // id for ubuntu-16-04-x64
    protected Integer doImageId;

    private static final int MAX_RETRIES = 200;

    private static final Logger log = LoggerFactory.getLogger(JMeterServiceImpl.class);

    private final DOAPIService doapiService;
    private final SSHClientService sshClientService;

    @Autowired
    private JMeterServiceImpl(final DOAPIService doapiService, final SSHClientService sshClientService) {
        this.doapiService = doapiService;
        this.sshClientService = sshClientService;
    }

    @Override
    public List<DropletResponse> createJMeterServerDroplets(int numDroplets, String size) {
        final List<DropletResponse> responses = new ArrayList<>();
        List<List<String>> dropletNames = DODropletNameUtil.dropletNumbers(JMETER_SERVER_BASE, numDroplets);
        dropletNames.forEach(names -> {
            responses.add(createJMeterDropletBase(names, JMETER_SERVER_BASE, size));
        });
        return responses;
    }

    @Override
    public DropletResponse createJMeterClientDroplet(String size) {
        return createJMeterDropletBase(Arrays.asList(JMETER_CLIENT), JMETER_CLIENT, size);
    }

    private DropletResponse createJMeterDropletBase(List<String> names, String tag, String size) {
        CreateDropletRequest request = CreateDropletRequestBuilder.builder()
            .region("nyc1")
            .size(size)
            .image(doImageId)
            .backups(false)
            .ipv6(false)
            .addTag(tag)
            .names(names)
            .build();
        try {
            return doapiService.createDroplets(request);
        } catch (IOException e) {
            log.error("Error Creating droplets: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> checkJMeterServerDropletsActive(int numDroplets) throws IOException, JSchException {
        return checkJMeterDropletsActive(JMETER_SERVER_BASE, numDroplets, "jmeter-server --version");
    }

    @Override
    public Map<String, Object> checkJMeterClientDropletActive() throws IOException, JSchException {
        return checkJMeterDropletsActive(JMETER_CLIENT, 1, "jmeter --version");
    }

    private Map<String, Object> checkJMeterDropletsActive(String tag, int expectedDroplets, String command) throws IOException, JSchException {
        Map<String, Object> ret = new HashMap<>();
        ret.put("expected_droplets", expectedDroplets);
        DropletResponse dropletResponse = doapiService.listDroplets(tag);
        // check created droplets against expected
        if (dropletResponse.getDroplets().size() < expectedDroplets) {
            ret.put("actual_droplets", dropletResponse.getDroplets().size());
            return ret;
        }
        ret.put("actual_droplets", expectedDroplets);

        // check for active status
        for (Droplet droplet : dropletResponse.getDroplets()) {
            if (!"active".equals(droplet.getStatus())) {
                ret.put("all_active", false);
                return ret;
            }
        }
        ret.put("all_active", true);

        // check for jmeter install
        List<String> ips = doapiService.getDropletIps(dropletResponse.getDroplets());
        for (String ip : ips) {
            String result = sshClientService.command(command, "root", ip);
            if (result == null || result.contains("currently not installed")) {
                ret.put("all_jmeter", false);
                return ret;
            }
        }

        ret.put("all_jmeter", true);
        return ret;
    }

    @Override
    public void addJMeterServersToFirewall() throws IOException {
        doapiService.addDropletsToFirewallByTags(Arrays.asList(JMETER_SERVER_BASE));
    }

    @Override
    public void addJMeterClientToFirewall() throws IOException {
        doapiService.addDropletsToFirewallByTags(Arrays.asList(JMETER_CLIENT));
    }

    @Override
    public String jMeterServersStart() throws IOException, JSchException {
        DropletResponse dropletResponse = doapiService.listDroplets(JMETER_SERVER_BASE);
        long dropletMemory = dropletResponse.getDroplets().get(0).getMemory();
        int jvmMemory = (int)(dropletMemory*JVM_MEMORY);

        List<String> ips = doapiService.getDropletIps(dropletResponse.getDroplets());

        for (String ip : ips) {
            String command = JMeterCommandUtil.serverCommand(jvmMemory, ip);
            int numTries = startJmeter(command, ip);
            if (numTries >= MAX_RETRIES) {
                log.error("jmeter server not started after {} tries for: {}", MAX_RETRIES, ip);
            }
        }

        return ips.stream().collect(Collectors.joining(","));
    }

    @Override
    public void jMeterClientCopyTestPlan() throws IOException, JSchException {
        DropletResponse dropletResponse = doapiService.listDroplets(JMETER_CLIENT);
        if (dropletResponse == null || dropletResponse.getDroplets() == null || dropletResponse.getDroplets().size() == 0 || dropletResponse.getDroplets().size() > 1) {
            log.error("Problem with retrieving jmeter client droplet.");
            return;
        }
        Droplet droplet = dropletResponse.getDroplets().get(0);
        String localIp = droplet.getDropletNetworks().getV4s().get(0).getIpAddress();

        int numTries = 0;
        do {
            sshClientService.scp(jMeterTestPlanFile, LOCAL_TEST_PLAN_NAME, "root", localIp);
            String result = sshClientService.command("ls /root/" + LOCAL_TEST_PLAN_NAME, "root", localIp);
            if (result != null && result.contains(LOCAL_TEST_PLAN_NAME)) {
                break;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                log.error("Failed to sleep: {}", e.getMessage(), e);
            }
        } while (numTries++ < MAX_RETRIES);
        if (numTries >= MAX_RETRIES) {
            log.error("jmeter client not started after {} tries for: {}", MAX_RETRIES, localIp);
        }
    }

    @Override
    public String jMeterClientStart(String remoteIps) throws IOException, JSchException {
        DropletResponse dropletResponse = doapiService.listDroplets(JMETER_CLIENT);
        if (
            dropletResponse == null || dropletResponse.getDroplets() == null ||
            dropletResponse.getDroplets().size() == 0 || dropletResponse.getDroplets().size() > 1
        ) {
            log.error("Problem with retrieving jmeter client droplet.");
            return null;
        }
        Droplet droplet = dropletResponse.getDroplets().get(0);
        long dropletMemory = droplet.getMemory();
        int jvmMemory = (int)(dropletMemory*JVM_MEMORY);
        String localIp = droplet.getDropletNetworks().getV4s().get(0).getIpAddress();

        String command = JMeterCommandUtil.clientCommand(jvmMemory, remoteIps, localIp);
        int numTries = startJmeter(command, localIp);
        if (numTries >= MAX_RETRIES) {
            log.error("jmeter client not started after {} tries for: {}", MAX_RETRIES, localIp);
            return null;
        }
        return localIp;
    }

    private int startJmeter(String command, String ip) throws IOException, JSchException {
        int numTries = 0;
        do {
            sshClientService.command(command, "root", ip);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                log.error("Failed to sleep: {}", e.getMessage(), e);
            }
            String result = sshClientService.command("ps aux | grep client.rmi.localport | grep -v grep", "root", ip);
            if (result != null && result.split("\n").length > 2) {
                break;
            }
        } while (numTries++ < MAX_RETRIES);
        return numTries;
    }
}
