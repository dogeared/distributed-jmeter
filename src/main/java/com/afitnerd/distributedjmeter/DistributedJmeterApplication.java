package com.afitnerd.distributedjmeter;

import com.afitnerd.distributedjmeter.service.SSHClient;
import com.jcraft.jsch.JSchException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class DistributedJmeterApplication {

	public static void main(String[] args) throws JSchException, IOException {
		ConfigurableApplicationContext ctx = SpringApplication.run(DistributedJmeterApplication.class, args);
		SSHClient sshClient = ctx.getBean(SSHClient.class);

//		sshClient.scp("/Users/dogeared/Projects/ipify_load_test/IPIFY.jmx", "jimjam.txt", "root", "138.197.45.227");
//        sshClient.command("cat jimjam.txt","root", "138.197.45.227");
	}
}
