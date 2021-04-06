package com.dyz.demo.rabbitmq.rpc;

import com.rabbitmq.client.Channel;

import java.io.IOException;

public class MyServer extends MQRpcServer{

    public MyServer(String name) throws IOException {
        super(connection.createChannel(), name);
    }

    @Override
    public String handleRequest(String request) {
        String response = "....this is server " + getServerName() + " response...";
        return response;
    }
}
