package com.dyz.demo.rabbitmq.confirm;

import com.dyz.demo.rabbitmq.common.RabbitMQConnection;
import com.dyz.demo.rabbitmq.common.consumer.CommonConsumer;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MainTestConfirm {

    public static void main(String[] args) throws IOException, TimeoutException {

        Connection connection = RabbitMQConnection.getConnection();
        Channel channel = connection.createChannel();

        // 创建交换器
        AMQP.Exchange.DeclareOk declareOk = channel.exchangeDeclare(
                "test-exchange",
                BuiltinExchangeType.TOPIC,
                false, // durable 交换器是否持久化
                true,  // autoDelete 自动删除，前提时没有队列或者交换器与该交换器绑定
                false, // internal 是否为内部交换器，客户端端无法直接发送消息到内部交换器，而是通过交换器路由的方式
                null); // Map<String, Objects>


        // 创建队列
        channel.queueDeclare(
                "test-queue-1",
                false, // durable 是否持久化
                false, // exclusive 排他性，true表示该队列只对声明它的connection可见
                true, // autoDelete 自动删除，前提是该队列没有消费者绑定且队列中没有消息
                null);

//        channel.queueDeclare(
//                "test-queue-2",
//                false, // durable 是否持久化
//                false, // exclusive 排他性，true表示该队列只对声明它的connection可见
//                true, // autoDelete 自动删除，前提是该队列没有消费者绑定
//                null);

        // 创建绑定
        AMQP.Queue.BindOk ok = channel.queueBind(
                "test-queue-1", // 队列
                "test-exchange", // 交换器
                "du.*", // binding key
                null);

//        channel.queueBind(
//                "test-queue-2", // 队列
//                "test-exchange", // 交换器
//                "yun.*", // binding key
//                null);

        // 接收消息
        channel.basicConsume(
                "test-queue-1", // 队列
                false, // auto ack 自动确认
                "tag1", // consumer tag 用于区分消费者
                false, // noLocal true，表示不能将同一个connection中生产者发送的消息传递给这个Connection中的消费者
                // AMQP标准，但是rabbitmq没有实现
                false, // exclusive 排他消费者，该队列只接受一个消费者
                null, // Map<String, Object> arguments
                new CommonConsumer(channel, "consumer-1") // consumer
        );


        // 发送消息
//        for (int i = 0; i < 1; i++) {
            new ConfirmProducer("test-exchange", "confirm-producer-1").sendMessage();
//        }
//        channel.basicPublish("test-exchange", "du.test", MessageProperties.TEXT_PLAIN.builder()
//                .build(), "test".getBytes());

    }
}
