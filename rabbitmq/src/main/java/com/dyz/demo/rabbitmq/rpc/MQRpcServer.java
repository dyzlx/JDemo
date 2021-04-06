package com.dyz.demo.rabbitmq.rpc;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public abstract class MQRpcServer extends DefaultConsumer implements MQRpc {

    private Channel channel = getChannel();

    private String serverName;

    public MQRpcServer(Channel channel, String name) throws IOException {
        super(channel);
        this.serverName = name;
        checkServerCommonExchange();
        declareServerQueueAndBind();
    }

    private void checkServerCommonExchange() throws IOException {
        try {
            channel.exchangeDeclarePassive(EXCHANGE_NAME);
        } catch (ShutdownSignalException | IOException e) {
            channel = connection.createChannel();
            channel.exchangeDeclare(
                    EXCHANGE_NAME,
                    BuiltinExchangeType.DIRECT,
                    true, // durable
                    false, // auto delete
                    false, // internal
                    null);
        }
    }

    private void declareServerQueueAndBind() throws IOException {
        String queueName = serverName + "." + UUID.randomUUID().toString();
        channel.queueDeclare(
                queueName,
                false, // durable
                true, // exclusive
                true, // auto delete
                null);
        channel.queueBind(
                queueName,
                EXCHANGE_NAME,
                serverName, // binding key
                null);
        channel.basicConsume(
                queueName,
                false, // auto ack
                serverName, // consumer tag
                false, // noLocal
                true, // exclusive
                null,
                this
        );
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                               byte[] body) throws IOException {
        String request = new String(body, StandardCharsets.UTF_8);
        System.out.printf("server [%s] get request [%s] \n", serverName, request);
        String response = handleRequest(request);
        getChannel().basicAck(envelope.getDeliveryTag(), false);
        String replyToQueueName = properties.getReplyTo();
        getChannel().addReturnListener(new ReturnListener() {
            @Override
            public void handleReturn(int replyCode, String replyType, String exchange, String routingKey, AMQP.BasicProperties basicProperties, byte[] body) throws IOException {
                System.out.printf("the response [%s] was returned", new String(body));
            }
        });
        getChannel().basicPublish(
                "", // 交换器名称 (使用默认交换器)
                replyToQueueName, // 路由键
                false,  // mandatory
                false,  // immediate
                MessageProperties.TEXT_PLAIN.builder().build(),
                response.getBytes()
        );
        System.out.printf("server [%s] send response [%s] \n", serverName, response);
        System.out.println();
    }

    public abstract String handleRequest(String request);

    public String getServerName() {
        return serverName;
    }
}
