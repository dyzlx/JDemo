package com.dyz.demo.rabbitmq.common;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.Objects;

public class RabbitMQConnection {

    private final static String HOST = "192.168.199.139";  // 192.168.199.139

    private static Connection CONNECTION;

    public static Connection getConnection() {
        if(Objects.isNull(CONNECTION)) {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(HOST);
            try {
                CONNECTION = factory.newConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return CONNECTION;
    }
}
