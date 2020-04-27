package com.github.maxiaoda;

import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientConnection extends Thread {
    private Socket socket;
    private int clineId;
    private String clineName;
    private Server server;

    public ClientConnection(int clineId, Server server, Socket socket) {
        this.socket = socket;
        this.server = server;
        this.clineId = clineId;
    }

    public int getClineId() {
        return clineId;
    }

    public void setClineId(int clineId) {
        this.clineId = clineId;
    }

    public String getClineName() {
        return clineName;
    }

    public void setClineName(String clineName) {
        this.clineName = clineName;
    }

    @Override
    public void run() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                if (clineName == null) {
                    clineName = line;
                    server.registerClient(this);
                    server.clientOnLine(this);
                } else {
                    Message message = JSON.parseObject(line, Message.class);
                    server.sendMessage(this, message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            server.clientOffLine(this);
        }
    }

    public void sendMessage(String message) throws IOException {
        Utils.writeMessage(socket,message);
    }
}
