package com.afitnerd.distributedjmeter.controller;

import com.afitnerd.distributedjmeter.model.request.CreateDropletRequest;
import com.afitnerd.distributedjmeter.model.response.Droplet;
import com.afitnerd.distributedjmeter.model.response.DropletResponse;
import com.afitnerd.distributedjmeter.service.DOAPIService;
import com.afitnerd.distributedjmeter.service.SSHClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class DOAPIController {

    @Autowired
    DOAPIService doapiService;

    @Autowired
    SSHClientService sshClientService;

    private static final Logger log = LoggerFactory.getLogger(DOAPIController.class);

    @RequestMapping(value = "/droplets", method = RequestMethod.GET)
    List<Droplet> listDroplets() throws IOException {
        return doapiService.listDroplets().getDroplets();
    }

    @RequestMapping(value = "/droplets", method = RequestMethod.POST)
    @ResponseBody DropletResponse createDroplets(@RequestBody CreateDropletRequest request) throws IOException {
        return doapiService.createDroplets(request);
    }

    @RequestMapping("/droplet_ips")
    @ResponseBody List<String> getDropletIps() throws IOException {
      return doapiService.getDropletIps();
    }

    @RequestMapping("/droplets_attribute")
    @ResponseBody List<Object> getDropletsAttribute(@RequestParam String attribute) throws IOException {
        return doapiService.getDropletsAttribute(attribute);
    }

    @RequestMapping("/droplets_attributes")
    @ResponseBody List<Map<String, Object>> getDropletsAttributes(@RequestParam List<String> attributes) throws IOException {
        return doapiService.getDropletsAttributes(attributes);
    }

    @RequestMapping(value = "/firewalls", method = RequestMethod.POST)
    @ResponseBody void addDropletsToFirewall(@RequestBody Map<String, List<Object>> dropletIds) throws IOException {
        doapiService.addDropletsToFirewall(dropletIds);
    }
}
