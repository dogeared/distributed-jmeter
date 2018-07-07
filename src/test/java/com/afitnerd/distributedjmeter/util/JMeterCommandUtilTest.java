package com.afitnerd.distributedjmeter.util;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class JMeterCommandUtilTest {

    @Test
    public void testClientCommand() {
        assertThat(
            JMeterCommandUtil.clientCommand(384, "1.1.1.1,2.2.2.2", "3.3.3.3"),
            is(
                "PATH=$PATH:/apache-jmeter/bin JVM_ARGS=\"-Xms384m -Xmx384m\" " +
                "nohup jmeter -n -t /root/test_plan.jmx -l /root/log_remote.jtl -R1.1.1.1,2.2.2.2 " +
                "-Dclient.continue_on_fail=true -Djava.rmi.server.hostname=3.3.3.3 -Dserver.rmi.ssl.disable=true " +
                "-Dclient.rmi.localport=4040 -Dserver.rmi.localport=4040 > /tmp/jmeter.log 2>&1 &"
            )
        );
    }

    @Test
    public void testServerCommand() {
        assertThat(
            JMeterCommandUtil.serverCommand(384, "3.3.3.3"),
            is(
                "PATH=$PATH:/apache-jmeter/bin JVM_ARGS=\"-Xms384m -Xmx384m\" " +
                "nohup jmeter-server -Djava.rmi.server.hostname=3.3.3.3 -Dserver.rmi.ssl.disable=true " +
                "-Dclient.rmi.localport=4040 -Dserver.rmi.localport=4040 > /tmp/jmeter.log 2>&1 &"
            )
        );
    }
}
