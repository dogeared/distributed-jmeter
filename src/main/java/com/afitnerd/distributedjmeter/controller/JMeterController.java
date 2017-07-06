package com.afitnerd.distributedjmeter.controller;

import com.afitnerd.distributedjmeter.model.response.DropletResponse;
import com.afitnerd.distributedjmeter.service.JMeterService;
import com.jcraft.jsch.JSchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class JMeterController {

    @Autowired
    JMeterService jMeterService;

    @RequestMapping(value = "/jmeter_server_create", method = RequestMethod.POST)
    @ResponseBody List<DropletResponse> jmeterServerCreate(@RequestBody Map<String, Object> parms) throws IOException {
        return jMeterService.createJMeterServerDroplets((Integer) parms.get("num_droplets"), (String) parms.get("size"));
    }

    @RequestMapping(value = "/jmeter_server_active")
    @ResponseBody Map<String, Object> jmeterServerActive(@RequestParam(value = "num_droplets") int numDroplets) throws IOException, JSchException {
        return jMeterService.checkJMeterServerDropletsActive(numDroplets);
    }

    @RequestMapping(value = "/jmeter_server_start", method = RequestMethod.POST)
    @ResponseBody String jmeterServerStart() throws IOException, JSchException {
        return jMeterService.jMeterServersStart();
    }
}
