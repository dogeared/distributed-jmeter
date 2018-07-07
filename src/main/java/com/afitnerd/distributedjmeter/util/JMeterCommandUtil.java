package com.afitnerd.distributedjmeter.util;

import java.util.Formatter;

public class JMeterCommandUtil {

    public static final String JMETER_HOME = "/apache-jmeter/bin";
    public static final String COMMAND_BASE = "PATH=$PATH:" + JMETER_HOME + " JVM_ARGS=\"-Xms%dm -Xmx%dm\" ";
    public static final String LOCAL_TEST_PLAN_NAME = "test_plan.jmx";
    public static final String LOG_NAME = "/tmp/jmeter.log";

    public static final String COMMON_ARGS =
        "-Djava.rmi.server.hostname=%s -Dserver.rmi.ssl.disable=true " +
        "-Dclient.rmi.localport=4040 -Dserver.rmi.localport=4040 " +
        "> " + LOG_NAME + " 2>&1 &";

    public static String clientCommand(int jvmMemory, String remoteIps, String localIp) {
        StringBuilder sbuf = new StringBuilder();
        Formatter fmt = new Formatter(sbuf);
        fmt.format(COMMAND_BASE, jvmMemory, jvmMemory);
        fmt.format("nohup jmeter -n -t /root/%s -l /root/log_remote.jtl -R%s ", LOCAL_TEST_PLAN_NAME, remoteIps);
        fmt.format("-Dclient.continue_on_fail=true ");
        fmt.format(COMMON_ARGS, localIp);
        return sbuf.toString();
    }

    public static String serverCommand(int jvmMemory, String localIp) {
        StringBuilder sbuf = new StringBuilder();
        Formatter fmt = new Formatter(sbuf);
        fmt.format(COMMAND_BASE, jvmMemory, jvmMemory);
        fmt.format("nohup jmeter-server ");
        fmt.format(COMMON_ARGS, localIp);
        return sbuf.toString();
    }
}
