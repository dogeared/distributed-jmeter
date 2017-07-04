package com.afitnerd.distributedjmeter.service;

import com.jcraft.jsch.JSchException;

import java.io.IOException;

public interface SSHClient {

    public void command(String command, String user, String host) throws JSchException, IOException;
    public void scp(String localFile, String remoteFile, String user, String host) throws JSchException, IOException;
}
