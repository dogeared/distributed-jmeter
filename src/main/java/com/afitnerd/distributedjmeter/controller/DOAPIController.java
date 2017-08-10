package com.afitnerd.distributedjmeter.controller;

import com.afitnerd.distributedjmeter.model.request.CreateDropletRequest;
import com.afitnerd.distributedjmeter.model.response.Droplet;
import com.afitnerd.distributedjmeter.model.response.DropletResponse;
import com.afitnerd.distributedjmeter.service.DOAPIService;
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

    private final DOAPIService doapiService;

    @Autowired
    public DOAPIController(final DOAPIService doapiService) {
        this.doapiService = doapiService;
    }

    @RequestMapping(value = "/droplets", method = RequestMethod.GET)
    List<Droplet> listDroplets(@RequestParam(required = false) String tagName) throws IOException {
        return doapiService.listDroplets(tagName).getDroplets();
    }

    @RequestMapping(value = "/droplets", method = RequestMethod.POST)
    @ResponseBody DropletResponse createDroplets(@RequestBody CreateDropletRequest request) throws IOException {
        return doapiService.createDroplets(request);
    }

    @RequestMapping(value = "/droplet_ips", method = RequestMethod.GET)
    @ResponseBody List<String> getDropletIps(@RequestParam(required = false) String tagName) throws IOException {
      return doapiService.getDropletIps(tagName);
    }

    @RequestMapping("/droplets_attribute")
    @ResponseBody List<?> getDropletsAttribute(@RequestParam String attribute) throws IOException {
        return doapiService.getDropletsAttribute(attribute);
    }

    @RequestMapping("/droplets_attributes")
    @ResponseBody List<Map<String, ?>> getDropletsAttributes(@RequestParam List<String> attributes) throws IOException {
        return doapiService.getDropletsAttributes(attributes);
    }

    @RequestMapping(value = "/firewalls", method = RequestMethod.POST)
    @ResponseBody void addDropletsToFirewall(@RequestBody List<Object> dropletIds) throws IOException {
        doapiService.addDropletsToFirewall(dropletIds);
    }
}
