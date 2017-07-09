package com.afitnerd.distributedjmeter.service;

import com.afitnerd.distributedjmeter.model.request.CreateDropletRequest;
import com.afitnerd.distributedjmeter.model.response.Droplet;
import com.afitnerd.distributedjmeter.model.response.DropletResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DOAPIServiceImpl implements DOAPIService {

    @Value("#{ @environment['do.token'] }")
    protected String doToken;

    @Value("#{ @environment['ssh.public.key.fingerprint'] }")
    protected String sshKeyFingerprint;

    @Value("#{ @environment['do.config.file'] }")
    protected String doConfigFile;

    @Value("#{ @environment['do.firewall.id'] }")
    protected String doFirewallId;

    @Autowired
    SSHClientService sshClientService;

    private static final String DO_API_BASE_URL = "https://api.digitalocean.com/v2";
    private static final String DO_DROPLET_ENDPOINT = "/droplets";
    private static final String DO_FIREWALL_ENDPOINT = "/firewalls";

    private ObjectMapper mapper = new ObjectMapper();
    private TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {};
    TypeReference<DropletResponse> dropletTypeRef = new TypeReference<DropletResponse>() {};

    private static final Logger log = LoggerFactory.getLogger(DOAPIServiceImpl.class);

    @Override
    public DropletResponse listDroplets() throws IOException {
        return listDroplets(null);
    }

    @Override
    public DropletResponse listDroplets(String tagName) throws IOException {
        String url = DO_API_BASE_URL + DO_DROPLET_ENDPOINT + "?per_page=200";
        if (tagName != null) { url += "&tag_name=" + tagName; }
        HttpResponse response = Request.Get(url)
            .addHeader("Authorization", "Bearer " + doToken)
            .addHeader("Content-type", "application/json")
            .execute()
            .returnResponse();

        return mapper.readValue(response.getEntity().getContent(), dropletTypeRef);
    }

    @Override
    public List<String> getDropletIps() throws IOException {
        return getDropletIps((String) null);
    }

    @Override
    public List<String> getDropletIps(String tagName) throws IOException {
        return getDropletIps(listDroplets(tagName).getDroplets());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> getDropletIps(List<Droplet> droplets) {
        return droplets.stream().map(elem -> {
            return elem.getDropletNetworks().getV4s().get(0).getIpAddress();
        }).collect(Collectors.toList());
    }

    @Override
    public List<Object> getDropletsAttribute(String attribute) throws IOException {
        return getDropletsAttribute(listDroplets().getDroplets(), attribute);
    }

    @Override
    public List<Object> getDropletsAttribute(List<Droplet> droplets, String attribute) {
        return droplets.stream().map(elem -> {
            try {
                return PropertyUtils.getProperty(elem, attribute);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                log.error("Problem returning '" + attribute + "' attribute: {}", e.getMessage(), e);
                return null;
            }
        }).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getDropletsAttributes(List<String> attributes) throws IOException {
        return listDroplets().getDroplets().stream().map(elem -> {
            Map<String, Object> attributeMap = new HashMap<>();
            for (String attribute : attributes) {
                try {
                    attributeMap.put(attribute,  PropertyUtils.getProperty(elem, attribute));
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    log.error("Problem returning '" + attribute + "' attribute: {}", e.getMessage(), e);
                    attributeMap.put(attribute,  null);
                }
            }
            return attributeMap;
        }).collect(Collectors.toList());
    }

    @Override
    public DropletResponse createDroplets(CreateDropletRequest request) throws IOException {

        if (request.getSshKeys().size() == 0 && sshKeyFingerprint != null) {
            request.getSshKeys().add(sshKeyFingerprint);
        }

        if (doConfigFile != null) {
            BufferedReader reader = new BufferedReader(new FileReader(doConfigFile));
            StringBuilder userData = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                userData.append(line + "\n");
            }
            reader.close();
            request.setUserData(userData.toString());
        }

        HttpResponse response = Request.Post(DO_API_BASE_URL + DO_DROPLET_ENDPOINT)
            .addHeader("Authorization", "Bearer " + doToken)
            .addHeader("Content-type", "application/json")
            .body(new StringEntity(mapper.writeValueAsString(request)))
            .execute()
            .returnResponse();

        return mapper.readValue(response.getEntity().getContent(), dropletTypeRef);
    }

    @Override
    public void addDropletsToFirewall(List<Object> dropletIds) throws IOException {
        Map<String, List<Object>> body = new HashMap<>();
        body.put("droplet_ids", dropletIds);
        HttpResponse response = Request.Post(DO_API_BASE_URL + DO_FIREWALL_ENDPOINT + "/" + doFirewallId + DO_DROPLET_ENDPOINT)
            .addHeader("Authorization", "Bearer " + doToken)
            .addHeader("Content-type", "application/json")
            .body(new StringEntity(mapper.writeValueAsString(body)))
            .execute()
            .returnResponse();
    }

    @Override
    public void addDropletsToFirewallByTags(List<String> tags) throws IOException {
        Map<String, List<String>> body = new HashMap<>();
        body.put("tags", tags);
        HttpResponse response = Request.Post(DO_API_BASE_URL + DO_FIREWALL_ENDPOINT + "/" + doFirewallId + "/tags")
                .addHeader("Authorization", "Bearer " + doToken)
                .addHeader("Content-type", "application/json")
                .body(new StringEntity(mapper.writeValueAsString(body)))
                .execute()
                .returnResponse();
    }
}
