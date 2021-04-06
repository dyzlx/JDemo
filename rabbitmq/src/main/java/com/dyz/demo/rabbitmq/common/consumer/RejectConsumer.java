package com.dyz.demo.rabbitmq.common.consumer;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;

public class RejectConsumer extends DefaultConsumer {

    private String consumerName = "consumer-reject-default";

    public RejectConsumer(Channel channel, String name) {
        super(channel);
        consumerName = name;
    }

    public RejectConsumer(Channel channel) {
        super(channel);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                               byte[] body) throws IOException {
        // 消息接收时被调用
        String message = new String(body, "UTF-8");
        System.out.printf("reject-consumer %s reject message %s from exchange %s, message routing key is %s, delivery tag is %s \n",
                consumerName,
                message,
                envelope.getExchange(),
                envelope.getRoutingKey(),
                envelope.getDeliveryTag());

        // reject message
        getChannel().basicReject(envelope.getDeliveryTag(), true);
    }
}
