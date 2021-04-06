package com.dyz.demo.rabbitmq.common;

import com.rabbitmq.client.AMQP;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Builder
@Data
public class CommonMessage {

    private String content;

    private String routingKey;

    private AMQP.BasicProperties basicProperties;
}
