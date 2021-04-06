package com.dyz.demo.rabbitmq.rpc;

import java.io.IOException;

public class ClientMain {

    public static void main(String[] args) throws IOException, InterruptedException {
        MQRpcClient mqRpcClient = new MQRpcClient("my-test-client");

        /**
         *
         * 每次发送wan完不sleep等一会的话，会出错。还未找到原因。
         *
         */
        mqRpcClient.request("my-test-server-A", "...request1...");

        Thread.sleep(1000);

        mqRpcClient.request("my-test-server-B", "...request2...");

        Thread.sleep(1000);

        mqRpcClient.request("my-test-server-A", "...request3...");

        Thread.sleep(1000);

        mqRpcClient.request("my-test-server-B", "...request4...");

        Thread.sleep(1000);
    }
}
