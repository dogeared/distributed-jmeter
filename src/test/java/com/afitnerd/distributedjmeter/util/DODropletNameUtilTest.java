package com.afitnerd.distributedjmeter.util;

import org.junit.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DODropletNameUtilTest {

    @Test
    public void test1Complete() {
        int numDroplets = DODropletNameUtil.CREATE_MULTIPLE_DROPLETS_MAX;
        List<List<String>> dropletNames = DODropletNameUtil.dropletNumbers("jmeter-server", numDroplets);
        assertThat(dropletNames.size(), is(1));
        assertThat(dropletNames.get(0).size(), is(numDroplets));
        IntStream.range(0, numDroplets).forEach(i -> {
            assertThat(dropletNames.get(0).get(i), is("jmeter-server-" + (i+1)));
        });
    }

    @Test
    public void test3Complete() {
        int numDroplets = DODropletNameUtil.CREATE_MULTIPLE_DROPLETS_MAX*3;
        List<List<String>> dropletNames = DODropletNameUtil.dropletNumbers("jmeter-server", numDroplets);
        assertThat(dropletNames.size(), is(3));
        assertThat(dropletNames.get(0).size(), is(numDroplets/3));
        IntStream.range(0, 3).forEach(i -> {
            IntStream.range(0, DODropletNameUtil.CREATE_MULTIPLE_DROPLETS_MAX).forEach(j -> {
                assertThat(dropletNames.get(i).get(j), is("jmeter-server-" + (j+1+(i*DODropletNameUtil.CREATE_MULTIPLE_DROPLETS_MAX))));
            });
        });
    }

    @Test
    public void testLessThan1Complete() {
        List<List<String>> dropletNames = DODropletNameUtil.dropletNumbers("jmeter-server", 1);
        assertThat(dropletNames.size(), is(1));
        assertThat(dropletNames.get(0).size(), is(1));
        assertThat(dropletNames.get(0).get(0), is("jmeter-server-1"));
    }

    @Test
    public void test2CompletePlus3() {
        int numDroplets = DODropletNameUtil.CREATE_MULTIPLE_DROPLETS_MAX*2+3;
        List<List<String>> dropletNames = DODropletNameUtil.dropletNumbers("jmeter-server", numDroplets);
        assertThat(dropletNames.size(), is(3));
        assertThat(dropletNames.get(0).size(), is(DODropletNameUtil.CREATE_MULTIPLE_DROPLETS_MAX));
        assertThat(dropletNames.get(1).size(), is(DODropletNameUtil.CREATE_MULTIPLE_DROPLETS_MAX));
        assertThat(dropletNames.get(2).size(), is(3));
        IntStream.range(0, 3).forEach(i -> {
            assertThat(dropletNames.get(2).get(i), is("jmeter-server-" + (21 + i)));
        });
    }
}
