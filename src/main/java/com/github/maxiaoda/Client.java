package com.github.maxiaoda;

import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        System.out.println("输入你的名字");
        Scanner userInput = new Scanner(System.in);
        String name = userInput.nextLine();

        Socket socket = new Socket("127.0.0.1", 8080);

        Utils.writeMessage(socket,name);

        System.out.println("连接成功！");

        new Thread(() -> readFromServer(socket)).start();

        while (true) {

            System.out.println("-----------------------------------");
            System.out.println("输入你要发送的消息");
            System.out.println("如：0:hello（格式：目标id+冒号+消息）");
            System.out.println("id=0代表向所有人发送消息");
            System.out.println("-----------------------------------");

            String line = userInput.nextLine();

            if (!line.contains(":")) {
                System.err.println("输入格式错误！");
            } else {
                int colonIndex = line.indexOf(':');
                int id = Integer.parseInt(line.substring(0, colonIndex));
                String message = line.substring(colonIndex + 1);

                String json = JSON.toJSONString(new Message(id, message));
                Utils.writeMessage(socket,json);
            }


        }
    }

    private static void readFromServer(Socket socket) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
