package com.dyz.demo.rabbitmq.rpc;

import com.dyz.demo.rabbitmq.common.RabbitMQConnection;
import com.rabbitmq.client.Connection;

public interface MQRpc {

    public static final String EXCHANGE_NAME = "rpc-exchange";

    public static final Connection connection = RabbitMQConnection.getConnection();
}
