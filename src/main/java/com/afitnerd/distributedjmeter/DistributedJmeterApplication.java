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
import org.springframework.scheduling.annotation.EnableAsync;
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
            doCommandLine((int) options.valueOf("droplets"), (String) options.valueOf("size"));
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

    private void doCommandLine(int droplets, String size) throws Exception {
        log.info("Creating {} JMeter Server Droplets", droplets);
        Future<List<DropletResponse>> createFuture = jMeterAsyncConfig.createJMeterServerDroplets(droplets, size);
        while (true) {
            if (createFuture.isDone()) {
                log.info("Done Creating {} JMeter Server Droplets", droplets);
                break;
            }
            log.info("... Still working on creating JMeter Server Droplets ...");
            Thread.sleep(2000);
        }

        log.info("Waiting for {} JMeter Servers to become active", droplets);
        Future<Map<String, Object>> checkActiveFuture = jMeterAsyncConfig.checkJMeterServerDropletsActive(droplets);
        while (true) {
            if (checkActiveFuture.isDone()) {
                log.info("All {} JMeter Server Droplets are active", droplets);
                break;
            }
            log.info("... Still working on making JMeter Server Droplets active ...");
            Thread.sleep(5000);
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
    }

    private OptionSet parseCommandLine(String[] args) {
        OptionParser parser = new OptionParser() {
            {
                accepts("droplets", "Number of JMeter Server Droplets to create")
                    .withRequiredArg().ofType(Integer.class)
                    .required();
                accepts("size", "The DigitOcean size of each droplet. Ex: 512mb, 1gb, 2gb, etc.")
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
            log.info("\nYou can pass parameters to this Spring Boot Application to launch JMeter Server instances:\n" + bao.toString());
        } catch (IOException e) {
            log.error("Error showing help: {}", e.getMessage());
        }
    }
}
