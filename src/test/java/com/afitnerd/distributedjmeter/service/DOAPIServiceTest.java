package com.afitnerd.distributedjmeter.service;

import com.afitnerd.distributedjmeter.annotation.UsesPost;
import com.afitnerd.distributedjmeter.annotation.UsesTag;
import com.afitnerd.distributedjmeter.model.request.CreateDropletRequest;
import com.afitnerd.distributedjmeter.model.request.CreateDropletRequestBuilder;
import com.afitnerd.distributedjmeter.model.response.DropletResponse;
import com.afitnerd.distributedjmeter.rule.BeforeSwitches;
import com.afitnerd.distributedjmeter.util.DODropletNameUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.afitnerd.distributedjmeter.service.DOAPIService.DO_API_BASE_URL;
import static com.afitnerd.distributedjmeter.service.DOAPIService.DO_DROPLET_ENDPOINT;
import static com.afitnerd.distributedjmeter.service.DOAPIService.DO_FIREWALL_ENDPOINT;
import static com.afitnerd.distributedjmeter.service.JMeterService.JMETER_SERVER_BASE;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;


@RunWith(PowerMockRunner.class)
@PrepareForTest(Request.class)
public class DOAPIServiceTest {

    Request request;
    Response response;
    HttpResponse httpResponse;
    HttpEntity httpEntity;
    DOAPIService doapiService;

    @Rule
    public BeforeSwitches beforeSwitches = new BeforeSwitches();

    private static final String TAG_NAME = "tag";
    private static final String DO_FIREWALL_ID = "do_firewall_id";

    @Before
    public void setup() throws IOException {
        mockStatic(Request.class);

        request = mock(Request.class);
        response = mock(Response.class);
        httpResponse = mock(HttpResponse.class);
        httpEntity = mock(HttpEntity.class);

        Resource dropletResponseFile = new ClassPathResource("DropletResponse.json");
        InputStream is = new FileInputStream(dropletResponseFile.getFile());


        if (beforeSwitches.usesPost()) {
            expect(Request.Post(beforeSwitches.url())).andReturn(request);
            expect(request.body(anyObject(HttpEntity.class))).andReturn(request);

            if (beforeSwitches.withResponse()) {
                expect(httpResponse.getEntity()).andReturn(httpEntity);
                expect(httpEntity.getContent()).andReturn(is);
            }
        } else { // usesGet or not specified (default)
            String url = DO_API_BASE_URL + DO_DROPLET_ENDPOINT + "?per_page=200";
            if (beforeSwitches.usesTag()) {
                url += "&tag_name=" + beforeSwitches.tag();
            }
            expect(Request.Get(url)).andReturn(request);
            expect(httpResponse.getEntity()).andReturn(httpEntity);
            expect(httpEntity.getContent()).andReturn(is);
        }

        expect(request.addHeader("Authorization", "Bearer abc")).andReturn(request);
        expect(request.addHeader("Content-type", "application/json")).andReturn(request);
        expect(request.execute()).andReturn(response);
        expect(response.returnResponse()).andReturn(httpResponse);

        replay(Request.class, request, response, httpResponse, httpEntity);

        doapiService = new DOAPIServiceImpl(
            null, "abc", "def", "do_config.yml", DO_FIREWALL_ID
        );
    }


    @UsesTag(TAG_NAME)
    @Test
    public void testListDropletsWithTag() throws IOException {
        DropletResponse dr = doapiService.listDroplets(TAG_NAME);

        verify(Request.class, request, response, httpResponse, httpEntity);
    }

    @Test
    public void testListDroplets() throws IOException {
        DropletResponse dr = doapiService.listDroplets();

        verify(Request.class, request, response, httpResponse, httpEntity);

        assertThat(dr.getDroplets().size(), is(2));
        assertThat(dr.getDroplets().get(0).getName(), is("example1.com"));
        assertThat(dr.getDroplets().get(1).getName(), is("example2.com"));
    }

    @Test
    public void testgetDropletIps() throws IOException {
        List<String> ips = doapiService.getDropletIps();

        verify(Request.class, request, response, httpResponse, httpEntity);

        assertThat(ips.get(0), is("104.236.32.182"));
        assertThat(ips.get(1), is("104.236.32.183"));
    }

    @Test
    public void testgetDropletsAttribute() throws IOException {
        List<List<String>> attrs = (List<List<String>>) doapiService.getDropletsAttribute("features");

        verify(Request.class, request, response, httpResponse, httpEntity);

        assertThat(attrs.get(0).get(0), is("backups"));
        assertThat(attrs.get(0).get(1), is("ipv6"));
        assertThat(attrs.get(0).get(2), is("virtio"));
    }

    @Test
    public void testgetDropletsAttribute_Fails() throws IOException {
        List attrs = doapiService.getDropletsAttribute("nope");

        verify(Request.class, request, response, httpResponse, httpEntity);

        assertThat(attrs.size(), is(2));
        assertThat(attrs.get(0), is(nullValue()));
        assertThat(attrs.get(1), is(nullValue()));
    }

    @Test
    public void testgetDropletsAttributes() throws IOException {
        List<Map<String, ?>> attrs = doapiService.getDropletsAttributes(Arrays.asList("id", "name"));

        verify(Request.class, request, response, httpResponse, httpEntity);

        assertThat(attrs.get(0).get("id"), is(3164444L));
        assertThat(attrs.get(0).get("name"), is("example1.com"));

        assertThat(attrs.get(1).get("id"), is(3164445L));
        assertThat(attrs.get(1).get("name"), is("example2.com"));
    }

    @Test
    public void testgetDropletsAttributes_Fail() throws IOException {
        List<Map<String, ?>> attrs = doapiService.getDropletsAttributes(Arrays.asList("id", "nope"));

        verify(Request.class, request, response, httpResponse, httpEntity);

        assertThat(attrs.get(0).get("id"), is(3164444L));
        assertThat(attrs.get(0).get("nope"), is(nullValue()));

        assertThat(attrs.get(1).get("id"), is(3164445L));
        assertThat(attrs.get(1).get("nope"), is(nullValue()));
    }

    @UsesPost(DO_API_BASE_URL + DO_FIREWALL_ENDPOINT + "/" + DO_FIREWALL_ID + DO_DROPLET_ENDPOINT)
    @Test
    public void testaddDropletsToFirewall() throws IOException {
        doapiService.addDropletsToFirewall(Arrays.asList(3164444L, 3164445L));

        verify(Request.class, request, response, httpResponse, httpEntity);
    }

    @UsesPost(DO_API_BASE_URL + DO_FIREWALL_ENDPOINT + "/" + DO_FIREWALL_ID + "/tags")
    @UsesTag(TAG_NAME)
    @Test
    public void testaddDropletsToFirewallWithTag() throws IOException {
        doapiService.addDropletsToFirewallByTags(Arrays.asList(TAG_NAME));

        verify(Request.class, request, response, httpResponse, httpEntity);
    }

    @UsesPost(value = DO_API_BASE_URL + DO_DROPLET_ENDPOINT, withResponse = true)
    @Test
    public void testcreateDroplets() throws IOException {
        CreateDropletRequest req = CreateDropletRequestBuilder.builder()
                .region("nyc3")
                .size("512mb")
                .image(26136050)
                .backups(false)
                .ipv6(false)
                .addTag(TAG_NAME)
                .names(Arrays.asList("test"))
                .build();

        doapiService.createDroplets(req);

        verify(Request.class, request, response, httpResponse, httpEntity);
    }
}
