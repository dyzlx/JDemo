package com.dyz.demo.rabbitmq.rpc;

import java.io.IOException;

public class ServerMain {

    public static void main(String[] args) throws IOException, InterruptedException {

        MQRpcServer mqRpcServerA = new MyServer("my-test-server-A");

        MQRpcServer mqRpcServerB = new MyServer("my-test-server-B");
    }
}
