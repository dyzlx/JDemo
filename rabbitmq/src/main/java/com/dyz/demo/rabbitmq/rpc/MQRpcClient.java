package com.dyz.demo.rabbitmq.rpc;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class MQRpcClient implements MQRpc {

    private String clientName;

    public MQRpcClient(String name) {
        this.clientName = name;
    }

    public String getClientName() {
        return clientName;
    }

    public String request(String serverName, String request) {
        System.out.printf("client [%s] send request [%s] to server [%s]\n", clientName, request, serverName);
        String response = null;
        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(1);
        Channel channel = null;
        try {
            channel = connection.createChannel();
            String queueName = clientName + ".reply_to." + UUID.randomUUID().toString();
            channel.queueDeclare(
                    queueName,
                    false, // durable
                    true, // exclusive
                    true, // auto delete
                    null);
            channel.addReturnListener(new ReturnListener() {
                @Override
                public void handleReturn(int replyCode, String replyType, String exchange, String routingKey,
                                         AMQP.BasicProperties basicProperties, byte[] body) throws IOException {
                    System.out.printf("the request [%s] was returned", new String(body));
                }
            });
            channel.basicPublish(
                    EXCHANGE_NAME, //
                    serverName, // routing key
                    true,  // mandatory
                    false,  // immediate
                    MessageProperties.TEXT_PLAIN.builder().replyTo(queueName).build(), // basicProperties
                    request.getBytes()
            );
            channel.basicConsume(
                    queueName, //
                    false, // auto ack
                    clientName, // consumer tag
                    false, // noLocal
                    true, // exclusive
                    null,
                    new DefaultConsumer(channel) {
                        public void handleDelivery(String consumerTag, Envelope envelope,
                                                   AMQP.BasicProperties properties, byte[] body) throws IOException {
                            String response = new String(body, StandardCharsets.UTF_8);
                            blockingQueue.offer(response);
                            getChannel().basicAck(envelope.getDeliveryTag(), false);
                            getChannel().basicCancel(consumerTag);
                        }
                    }
            );
            response = blockingQueue.poll(3, TimeUnit.SECONDS);
            if(Objects.isNull(response)) {
                System.out.println("get response timeout");
                return null;
            }
            System.out.printf("client [%s] receive response [%s] from server [%s]\n", clientName, response, serverName);
            System.out.println();
        } catch (Exception e) {
            System.out.println("request error!");
            e.printStackTrace();
        }
        finally {
            try {
                if(Objects.nonNull(channel)) {
                    channel.close();
                }
            } catch (IOException | ShutdownSignalException ignored) {
                // ignored exception
            } catch (Exception e) {
                System.out.println("request error! channel close error!");
            }
        }
        return response;
    }
}
