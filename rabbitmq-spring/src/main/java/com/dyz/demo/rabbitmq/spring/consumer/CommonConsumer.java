package com.dyz.demo.rabbitmq.spring.consumer;


import com.dyz.demo.rabbitmq.spring.model.CommonMessage;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RabbitListener(queues = "test_queue_a")
public class CommonConsumer {

    private String name = "default_consumer";

    public CommonConsumer() {}

    public CommonConsumer(String name) {
        this.name = name;
    }

    @RabbitHandler
    public void consumeString(String message) {
        log.info("common consumer get string message: {}", message);
    }

    @RabbitHandler
    public void consumeCommonMessage(@Header(AmqpHeaders.DELIVERY_TAG) Long deliveryTag, CommonMessage message, Channel channel)
            throws IOException {
        log.info("common consumer get common object message: {}. deliveryTag={}", message, deliveryTag);
        channel.basicAck(deliveryTag, false);
    }
}
