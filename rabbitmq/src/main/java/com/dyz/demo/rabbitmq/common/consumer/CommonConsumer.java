package com.dyz.demo.rabbitmq.common.consumer;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

import java.io.IOException;
import java.text.MessageFormat;

public class CommonConsumer extends DefaultConsumer {

    private String consumerName = "consumer-default";

    public CommonConsumer(Channel channel, String name) {
        super(channel);
        consumerName = name;
    }

    public CommonConsumer(Channel channel) {
        super(channel);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                               byte[] body) throws IOException {
        // 消息接收时被调用
        String message = new String(body, "UTF-8");
//        System.out.printf("consumer %s [tag: %s]receive message %s from exchange %s, message routing key is %s, delivery tag is %s \n",
//                consumerName,
//                consumerTag,
//                message,
//                envelope.getExchange(),
//                envelope.getRoutingKey(),
//                envelope.getDeliveryTag());
        getChannel().basicAck(envelope.getDeliveryTag(), false);
    }

    @Override
    public void handleConsumeOk(String consumerTag) {
        // 任意basicConsume()调用导致消费者被注册时调用。
    }

    @Override
    public void handleCancelOk(String consumerTag) {
        // basicCancel()调用导致的订阅取消时被调用。
    }

    @Override
    public void handleCancel(String consumerTag) throws IOException {
        // 除了调用basicCancel()的其他原因导致消息被取消时调用。
    }

    @Override
    public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
        // 当Channel与Connection关闭的时候会调用。
    }

    @Override
    public void handleRecoverOk(String consumerTag) {
        // basic.recover-ok被接收时调用
    }
}
