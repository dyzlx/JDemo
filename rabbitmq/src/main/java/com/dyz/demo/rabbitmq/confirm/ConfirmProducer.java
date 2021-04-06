package com.dyz.demo.rabbitmq.confirm;

import com.dyz.demo.rabbitmq.common.CommonMessage;
import com.dyz.demo.rabbitmq.common.Messages;
import com.dyz.demo.rabbitmq.common.RabbitMQConnection;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.ReturnListener;

import java.io.IOException;

public class ConfirmProducer {
    private String producerName = "confirm-producer-default";

    private String exchangeName;

    public ConfirmProducer(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public ConfirmProducer(String exchangeName, String producerName) {
        this.exchangeName = exchangeName;
        this.producerName = producerName;
    }

    public void sendMessage() throws IOException {
        Connection connection = RabbitMQConnection.getConnection();
        Channel channel = connection.createChannel();
        // 将信道设置为confirm模式
        channel.confirmSelect();
        channel.addReturnListener(new ReturnListener() {
            @Override
            public void handleReturn(int replyCode, String replyText, String exchange, String routingKey, AMQP.BasicProperties properties, byte[] body) {
                String returnMsg=new String(body);
                System.out.printf("confirm-producer %s receive a return message %s \n", producerName, returnMsg);
            }
        });
        channel.addConfirmListener(new ConfirmListener() {
            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                System.out.printf("confirm message, delivery tag = %s ,multiple ?= %s \n", deliveryTag, multiple);
            }
            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                System.out.printf("not confirm message, delivery tag = %s ,multiple ?= %s \n", deliveryTag, multiple);
            }
        });
        for(CommonMessage message : Messages.get()) {
            System.out.printf("confirm-producer %s send message %s, routing key = %s \n", producerName, message.getContent(), message.getRoutingKey());
            channel.basicPublish(
                    exchangeName,
                    message.getRoutingKey(),
                    true,  // mandatory
                    // true时，交换器无法根据自动的类型和路由键找到一个符合条件的队列，那么RabbitMq会调用Basic.Ruturn命令将消息返回给生产都
                    // 生产者可以通过channel.addReturnListener方法监听返回给生产者的消息
                    // false时，出现上述情况消息被直接丢弃
                    false,  // immediate
                    // true时，如果exchange在将消息路由到queue(s)时发现对于的queue上么有消费者，那么这条消息不会放入队列中
                    // 当与消息routeKey关联的所有queue（一个或者多个）都没有消费者时，该消息会通过basic.return方法返还给生产者
                    // RabbitMQ 3.0版本开始去掉了对immediate的支持
                    MessageProperties.TEXT_PLAIN.builder().build(), // basicProperties
                    message.getContent().getBytes()
            );
//            try {
//                if(channel.waitForConfirms()) {
//                    System.out.println("message confirmed");
//                }
//            } catch (Exception e) {
//                System.out.println("message not confirm!");
//            }

//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }

//        try {
//            channel.waitForConfirmsOrDie();
//            System.out.println("messages all confirmed");
//        } catch (InterruptedException e) {
//            System.out.println("message not confirmed");
//        }
    }
}
