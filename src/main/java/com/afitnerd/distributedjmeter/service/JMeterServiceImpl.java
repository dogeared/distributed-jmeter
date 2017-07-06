package com.afitnerd.distributedjmeter.service;

import com.afitnerd.distributedjmeter.model.request.CreateDropletRequest;
import com.afitnerd.distributedjmeter.model.request.CreateDropletRequestBuilder;
import com.afitnerd.distributedjmeter.model.response.Droplet;
import com.afitnerd.distributedjmeter.model.response.DropletResponse;
import com.afitnerd.distributedjmeter.util.DODropletNameUtil;
import com.jcraft.jsch.JSchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JMeterServiceImpl implements JMeterService {

    @Autowired
    DOAPIService doapiService;

    @Autowired
    SSHClientService sshClientService;

    private static final Logger log = LoggerFactory.getLogger(JMeterServiceImpl.class);

    @Override
    public List<DropletResponse> createJMeterServerDroplets(int numDroplets, String size) {
        final List<DropletResponse> responses = new ArrayList<>();
        List<List<String>> dropletNames = DODropletNameUtil.dropletNumbers(JMETER_SERVER_BASE, numDroplets);
        dropletNames.forEach(names -> {
            CreateDropletRequest request = CreateDropletRequestBuilder.builder()
                    .region("nyc3")
                    .size(size)
                    .image("ubuntu-16-04-x64")
                    .backups(false)
                    .ipv6(false)
                    .addTag(JMETER_SERVER_BASE)
                    .names(names)
                    .build();
            try {
                responses.add(doapiService.createDroplets(request));
            } catch (IOException e) {
                log.error("Error Creating droplets: {}", e.getMessage(), e);
            }
        });
        return responses;
    }

    @Override
    public Map<String, Object> checkJMeterServerDropletsActive(int numDroplets) throws IOException, JSchException {
        Map<String, Object> ret = new HashMap<>();
        ret.put("expected_droplets", numDroplets);
        DropletResponse dropletResponse = doapiService.listDroplets(JMETER_SERVER_BASE);
        // check created droplets against expected
        if (dropletResponse.getDroplets().size() < numDroplets) {
            ret.put("actual_droplets", dropletResponse.getDroplets().size());
            return ret;
        }
        ret.put("actual_droplets", numDroplets);

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
            String result = sshClientService.command("jmeter-server --version", "root", ip);
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
    public String jMeterServersStart() throws IOException, JSchException {
        DropletResponse dropletResponse = doapiService.listDroplets(JMETER_SERVER_BASE);
        long dropletMemory = dropletResponse.getDroplets().get(0).getMemory();
        int jvmMemory = (int)(dropletMemory*0.75f);

        List<String> ips = doapiService.getDropletIps(dropletResponse.getDroplets());

        String command = "PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/apache-jmeter-3.2/bin " +
                "JVM_ARGS=\"-Xms"+ jvmMemory + "m -Xmx" + jvmMemory + "m\" " +
                "nohup jmeter-server -Dserver.rmi.localport=4040 -Djava.rmi.server.hostname={ip} " +
                "> /tmp/jmeter-server.log 2>&1 &";

        for (String ip : ips) {
            String uniqueCommand = command.replace("{ip}", ip);
            int numTries = 0;
            do {
                sshClientService.command(uniqueCommand, "root", ip);
                String result = sshClientService.command("ps auxw | grep jmeter-server", "root", ip);
                if (result != null && result.split("\n").length > 2) {
                    break;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    log.error("Failed to sleep: {}", e.getMessage(), e);
                }
            } while (numTries++ < 200);
            if (numTries >= 200) {
                log.error("jmeter server not started after 200 tries for: {}", ip);
            }
        }

        return ips.stream().collect(Collectors.joining(","));
    }
}
