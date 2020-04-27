package com.github.maxiaoda;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Server {
    private static AtomicInteger COUNTER = new AtomicInteger(0);
    private final ServerSocket serverSocket;
    private final Map<Integer, ClientConnection> clients = new ConcurrentHashMap<>();

    //TCP连接端口号：0~65535
    public Server(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    public void start() throws IOException {
        while (true) {
            Socket socket = serverSocket.accept();
            new ClientConnection(COUNTER.incrementAndGet(), this, socket).start();
        }
    }

    public static void main(String[] args) throws IOException {
        new Server(8080).start();
    }

    public void registerClient(ClientConnection clientConnection) {
        clients.put(clientConnection.getClineId(), clientConnection);
    }

    public void clientOnLine(ClientConnection clientWhoHasJustLoggedIn) {
        clients.values().forEach(client ->
                dispatchMessage(client, "系统", "所有人", clientWhoHasJustLoggedIn.getClineName() + "上线了！" + getAllClientInfo()));
    }

    public void sendMessage(ClientConnection src, Message message) {
        if (message.getId() == 0) {
            clients.values().forEach(client ->
                    dispatchMessage(client, src.getClineName(), "所有人", message.getMassage()));
        } else {
            int targetUser = message.getId();
            ClientConnection target = clients.get(targetUser);
            if (target == null) {
                System.err.println("用户" + targetUser + "已下线或不存在");
            } else {
                dispatchMessage(target, src.getClineName(), "你", message.getMassage());
            }
        }
    }

    public void clientOffLine(ClientConnection clientConnection) {
        clients.remove(clientConnection.getClineId());
        clients.values().forEach(client ->
                dispatchMessage(client, "系统", "所有人", clientConnection.getClineName() + "下线了！" + getAllClientInfo()));
    }

    private void dispatchMessage(ClientConnection client, String src, String target, String message) {
        try {
            client.sendMessage(src + "@" + target + "说：" + message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getAllClientInfo() {
        return clients.entrySet().stream().map(entry -> entry.getKey() + ":" + entry.getValue().getClineName()).collect(Collectors.joining(","));
    }

}
