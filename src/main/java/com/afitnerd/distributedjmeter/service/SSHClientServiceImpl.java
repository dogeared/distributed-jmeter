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
public class SSHClientServiceImpl implements SSHClientService {

    @Value("#{ @environment['ssh.private.key.file'] }")
    protected String sshPrivateKeyFile;

    private static final int MAX_RETRIES = 200;

    private JSch jsch;

    private static final Logger log = LoggerFactory.getLogger(SSHClientServiceImpl.class);

    @PostConstruct
    void setup() throws JSchException {
        jsch=new JSch();
        jsch.addIdentity(sshPrivateKeyFile);
    }

    public String command(String command, String user, String host) {
        int retry = 0;
        do {
            try {
                Session session=jsch.getSession(user, host, 22);
                session.setConfig("StrictHostKeyChecking", "no");
                session.connect();
                Channel channel=session.openChannel("exec");
                ((ChannelExec)channel).setCommand(command);

                InputStream is = channel.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                channel.connect();
                String line = null;
                StringBuilder ret = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    ret.append(line + "\n");
                }
                channel.disconnect();
                session.disconnect();
                return ret.toString();
            } catch (IOException | JSchException e) {
                log.error("Caught ssh exception. Will retry. - {}", e.getMessage());
            }
        } while (retry++ < MAX_RETRIES);
        log.error("retry max {} exceeded. Unable to continue.", MAX_RETRIES);
        return null;
    }

    public void scp(String localFile, String remoteFile, String user, String host) {
        int retry = 0;
        do {
            try {

                Session session=jsch.getSession(user, host, 22);
                session.setConfig("StrictHostKeyChecking", "no");
                session.connect();
                Channel channel=session.openChannel("exec");

                ((ChannelExec)channel).setCommand("scp -t " + remoteFile);

                OutputStream out = channel.getOutputStream();
                InputStream in = channel.getInputStream();

                channel.connect();

                if (checkAck(in) != 0) {
                    log.error("Error setting up scp {} to {}:{}", localFile, host, remoteFile);
                    return;
                }

                File lfile = new File(localFile);
                long filesize = lfile.length();
                String command = "C0644 " + filesize + " " + remoteFile + "\n";
                out.write(command.getBytes());
                out.flush();

                if (checkAck(in) != 0) {
                    log.error("Error executing: {}", command);
                    return;
                }

                FileInputStream fis = new FileInputStream(localFile);

                byte[] buf = new byte[1024];
                while(true){
                    int len = fis.read(buf, 0, buf.length);
                    if(len <= 0) break;
                    out.write(buf, 0, len);
                }
                fis.close();
                fis = null;
                buf[0]=0;
                out.write(buf, 0, 1);
                out.flush();

                if (checkAck(in) != 0) {
                    log.error("Error completing file transfer.");
                    return;
                }

                out.close();

                channel.disconnect();
                session.disconnect();
                return;
            } catch (IOException | JSchException e) {
                log.error("Caught ssh exception. Will retry. - {}", e.getMessage());
            }
        } while (retry++ < MAX_RETRIES);
        log.error("retry max {} exceeded. Unable to continue.", MAX_RETRIES);
    }

    private int checkAck(InputStream in) throws IOException{
        int b = in.read();
        // b may be 0 for success,
        //          1 for error,
        //          2 for fatal error,
        //          -1
        if (b == 0 || b == -1) { return b; }

        if (b == 1 || b == 2) {
            StringBuffer sb = new StringBuffer();
            int c;
            do {
                c = in.read();
                sb.append((char)c);
            } while(c != '\n');
            log.error("Error transferring file: {}", sb.toString());
        }
        return b;
    }
}
