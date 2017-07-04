package com.afitnerd.distributedjmeter.service;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

@Service
public class SSHClientImpl implements SSHClient {

    @Value("#{ @environment['ssh.private.key.file'] }")
    protected String sshPrivateKeyFile;

    private JSch jsch;

    private static final Logger log = LoggerFactory.getLogger(SSHClientImpl.class);

    @PostConstruct
    void setup() throws JSchException {
        jsch=new JSch();
        jsch.addIdentity(sshPrivateKeyFile);
    }

    public void command(String command, String user, String host) throws JSchException, IOException {
        Session session=jsch.getSession(user, host, 22);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();
        Channel channel=session.openChannel("exec");
        ((ChannelExec)channel).setCommand(command);

        InputStream is = channel.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        channel.connect();
        String line = null;
        while ((line = reader.readLine()) != null) {
            log.info(line);
        }
        channel.disconnect();
        session.disconnect();
    }

    public void scp(String localFile, String remoteFile, String user, String host) throws JSchException, IOException {
        Session session=jsch.getSession(user, host, 22);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();
        Channel channel=session.openChannel("exec");

        ((ChannelExec)channel).setCommand("scp -t " + remoteFile);

        OutputStream out = channel.getOutputStream();
        InputStream in = channel.getInputStream();

        channel.connect();

        File lfile = new File(localFile);
        long filesize = lfile.length();
        String command = "C0644 " + filesize + " " + remoteFile + "\n";
        out.write(command.getBytes());
        out.flush();

        FileInputStream fis = new FileInputStream(localFile);

        byte[] buf=new byte[1024];
        while(true){
            int len=fis.read(buf, 0, buf.length);
            if(len<=0) break;
            out.write(buf, 0, len);
        }
        fis.close();
        fis = null;
        buf[0]=0;
        out.write(buf, 0, 1);
        out.flush();
        out.close();

        channel.disconnect();
        session.disconnect();
    }
}
