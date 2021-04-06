package com.dyz.demo.rabbitmq.ack;

import com.dyz.demo.rabbitmq.common.producer.CommonProducer;
import com.dyz.demo.rabbitmq.common.RabbitMQConnection;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MainTestAck {

    public static void main(String[] args) throws IOException, TimeoutException {

        Connection connection_ack_demo = RabbitMQConnection.getConnection();
        Channel channel = connection_ack_demo.createChannel();

        // 创建交换器
        channel.exchangeDeclare(
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
                false, // autoDelete 自动删除，前提是该队列没有消费者绑定
                null);

//        channel.queueDeclare(
//                "test-queue-2",
//                false, // durable 是否持久化
//                false, // exclusive 排他性，true表示该队列只对声明它的connection可见
//                true, // autoDelete 自动删除，前提是该队列没有消费者绑定
//                null);

        // 创建绑定
        channel.queueBind(
                "test-queue-1", // 队列
                "test-exchange", // 交换器
                "du.*", // binding key
                null);

//        channel.queueBind(
//                "test-queue-2", // 队列
//                "test-exchange", // 交换器
//                "yun.*", // binding key
//                null);

        channel.basicQos(0,10, false);
        // 接收消息
        channel.basicConsume(
                "test-queue-1", // 队列
                false, // auto ack 自动确认
                "tag1-ack", // consumer tag 用于区分消费者
                false, // noLocal true，表示不能将同一个connection中生产者发送的消息传递给这个Connection中的消费者
                           // AMQP标准，但是rabbitmq没有实现
                false, // exclusive 排他消费者，该队列只接受一个消费者
                null, // Map<String, Object> arguments
                new AckConsumer(channel, "consumer-1") // consumer
        );

//        channel.basicConsume(
//                "test-queue-1", // 队列
//                false, // auto ack 自动确认
//                "tag2-ack", // consumer tag 用于区分消费者
//                false, // noLocal true，表示不能将同一个connection中生产者发送的消息传递给这个Connection中的消费者
//                           // AMQP标准，但是rabbitmq没有实现
//                false, // exclusive 排他消费者，该队列只接受一个消费者
//                null, // Map<String, Object> arguments
//                new CommonConsumer(channel, "consumer-2") // consumer
//        );
//        GetResponse response = channel.basicGet("test-queue-1", true); //队列名称和是否自动确认
//        System.out.println(new String(response.getBody()) + " " + response.getMessageCount());

//        channel.close();
//        connection.close();

        // 发送消息
        new CommonProducer("test-exchange", "producer-1").sendMessage();
    }
}
