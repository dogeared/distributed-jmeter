package com.afitnerd.distributedjmeter.controller;

import com.afitnerd.distributedjmeter.service.SSHClientService;
import com.jcraft.jsch.JSchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/api/v1")
public class SSHController {

    @Autowired
    SSHClientService sshClientService;

    @RequestMapping(value = "/ssh_command", method = POST)
    @ResponseBody public String sendCommandToMachines(@RequestBody Map<String, Object> request) throws IOException, JSchException {
        String command = (String) request.get("command");
        String host = (String) request.get("host");
        String user = (String) request.get("user");
        return sshClientService.command(command, user, host);
    }
}
