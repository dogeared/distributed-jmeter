package com.afitnerd.distributedjmeter;

import com.afitnerd.distributedjmeter.config.JMeterAsyncConfig;
import com.afitnerd.distributedjmeter.model.response.DropletResponse;
import com.jcraft.jsch.JSchException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

@SpringBootApplication
public class DistributedJmeterApplication implements CommandLineRunner {

    @Autowired
    JMeterAsyncConfig jMeterAsyncConfig;

    private static final Logger log = LoggerFactory.getLogger(DistributedJmeterApplication.class);

	public static void main(String[] args) throws JSchException, IOException {
        SpringApplication.run(DistributedJmeterApplication.class, args);
	}

    @Override
    public void run(String... args) throws Exception {
        OptionSet options = parseCommandLine(args);
        if (options != null) {
            String remoteIps = doCommandLineServer(
                (int) options.valueOf("server-droplets"),
                (String) options.valueOf("server-size")
            );

            doCommandLineClient(
                (String) options.valueOf("client-size"),
                remoteIps
            );
        }
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        return executor;
    }

    private String doCommandLineServer(int serverDroplets, String serverSize) throws Exception {
        log.info("Creating {} JMeter Server Droplets", serverDroplets);
        Future<List<DropletResponse>> createFuture =
                jMeterAsyncConfig.createJMeterServerDroplets(serverDroplets, serverSize);
        while (true) {
            if (createFuture.isDone()) {
                log.info("Done Creating {} JMeter Server Droplets", serverDroplets);
                break;
            }
            log.info("... Still working on creating JMeter Server Droplets ...");
            Thread.sleep(2000);
        }

        log.info("Waiting for {} JMeter Server Droplets to become active", serverDroplets);
        Future<Map<String, Object>> checkActiveFuture =
                jMeterAsyncConfig.checkJMeterServerDropletsActive(serverDroplets);
        while (true) {
            if (checkActiveFuture.isDone()) {
                log.info("All {} JMeter Server Droplets are active", serverDroplets);
                break;
            }
            log.info("... Still working on making JMeter Server Droplets active ...");
            Thread.sleep(5000);
        }

        log.info("Adding JMeter Servers to Firewall");
        Future<String> addJMeterToFirewallFuture = jMeterAsyncConfig.addJMeterServersToFirewall();
        while (true) {
            if (addJMeterToFirewallFuture.isDone()) {
                log.info("Added JMeter Servers to Firewall.");
                break;
            }
            log.info("... Still adding JMeter Servers to Firewall ...");
            Thread.sleep(1000);
        }

        log.info("Starting JMeter Servers");
        Future<String> startJMeterFuture = jMeterAsyncConfig.jMeterServersStart();
        while (true) {
            if (startJMeterFuture.isDone()) {
                log.info("All JMeter Servers are started");
                log.info("IP Addresses: {}", startJMeterFuture.get());
                break;
            }
            log.info("... Still starting JMeter Servers ...");
            Thread.sleep(5000);
        }
        return startJMeterFuture.get();
    }

    private void doCommandLineClient(String clientSize, String remoteIps) throws Exception {
        log.info("Creating JMeter Client Droplet");
        Future<DropletResponse> createClientFuture =
            jMeterAsyncConfig.createJMeterClientDroplet(clientSize);
        while (true) {
            if (createClientFuture.isDone()) {
                log.info("Done Creating JMeter Client Droplet");
                break;
            }
            log.info("... Still working on creating JMeter Client Droplet ...");
            Thread.sleep(2000);
        }

        log.info("Waiting for JMeter Client Droplet to become active");
        Future<Map<String, Object>> checkActiveClientFuture =
            jMeterAsyncConfig.checkJMeterClientDropletActive();
        while (true) {
            if (checkActiveClientFuture.isDone()) {
                log.info("JMeter Client Droplet is active");
                break;
            }
            log.info("... Still working on making JMeter Client Droplet active ...");
            Thread.sleep(5000);
        }

        log.info("Adding JMeter Client to Firewall");
        Future<String> addJMeterClientToFirewallFuture = jMeterAsyncConfig.addJMeterClientToFirewall();
        while (true) {
            if (addJMeterClientToFirewallFuture.isDone()) {
                log.info("Added JMeter Client to Firewall.");
                break;
            }
            log.info("... Still adding JMeter Client to Firewall ...");
            Thread.sleep(1000);
        }

        log.info("Copying test plan for JMeter Client");
        Future<String> jMeterClientCopyTestPlanFuture = jMeterAsyncConfig.jMeterClientCopyTestPlan();
        while (true) {
            if (jMeterClientCopyTestPlanFuture.isDone()) {
                log.info("Copied test plan to JMeter Client");
                break;
            }
            log.info("... Still copying test plan to JMeter Client ...");
            Thread.sleep(2000);
        }

        log.info("Starting JMeter Client");
        Future<String> startJMeterClientFuture = jMeterAsyncConfig.jMeterClientStart(remoteIps);
        while (true) {
            if (startJMeterClientFuture.isDone()) {
                log.info("JMeter Client is started");
                break;
            }
            log.info("... Still starting JMeter Client ...");
            Thread.sleep(5000);
        }
    }

    private OptionSet parseCommandLine(String[] args) {
        OptionParser parser = new OptionParser() {
            {
                accepts("server-droplets", "Number of JMeter Server Droplets to create")
                    .withRequiredArg().ofType(Integer.class)
                    .required();
                accepts("server-size", "The DigitOcean size of each droplet. Ex: 512mb, 1gb, 2gb, etc.")
                    .withRequiredArg().ofType(String.class)
                    .required();
                accepts("client-size", "The DigitOcean size of the client droplet. Ex: 512mb, 1gb, 2gb, etc.")
                    .withRequiredArg().ofType(String.class)
                    .required();
            }
        };

        try {
            return parser.parse(args);
        } catch (Exception e) {
            log.error("Error processing args: {}", e.getMessage());
            showHelp(parser);
            return null;
        }
    }

    private void showHelp(OptionParser parser) {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        try {
            parser.printHelpOn(bao);
            log.info("\nYou can pass parameters to this Spring Boot Application to launch JMeter instances:\n" + bao.toString());
        } catch (IOException e) {
            log.error("Error showing help: {}", e.getMessage());
        }
    }
}
