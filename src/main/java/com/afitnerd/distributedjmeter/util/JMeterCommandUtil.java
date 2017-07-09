package com.afitnerd.distributedjmeter.util;

import java.util.Formatter;

public class JMeterCommandUtil {

    public static final String JMETER_HOME = "/apache-jmeter-3.2/bin";
    public static final String COMMAND_BASE = "PATH=$PATH:" + JMETER_HOME + " JVM_ARGS=\"-Xms%dm -Xmx%dm\" ";

    public static String clientCommand(int jvmMemory, String remoteIps, String localIp) {
        StringBuilder sbuf = new StringBuilder();
        Formatter fmt = new Formatter(sbuf);
        fmt.format(COMMAND_BASE, jvmMemory, jvmMemory);
        fmt.format("nohup jmeter -n -t /root/IPIFY.jmx -l /root/log_remote.jtl -R%s ", remoteIps);
        fmt.format("-Djava.rmi.server.hostname=%s -Dclient.rmi.localport=4040 -Dserver.rmi.localport=4040 ", localIp);
        sbuf.append("> /tmp/jmeter-client.log 2>&1 &");
        return sbuf.toString();
    }

    public static String serverCommand(int jvmMemory, String localIp) {
        StringBuilder sbuf = new StringBuilder();
        Formatter fmt = new Formatter(sbuf);
        fmt.format(COMMAND_BASE, jvmMemory, jvmMemory);
        fmt.format("nohup jmeter-server -Djava.rmi.server.hostname=%s ", localIp);
        sbuf.append("-Dclient.rmi.localport=4040 -Dserver.rmi.localport=4040 > /tmp/jmeter-server.log 2>&1 &");
        return sbuf.toString();
    }
}
