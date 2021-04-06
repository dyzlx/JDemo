package com.dyz.demo.rabbitmq.spring.producer;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class CommonProducer implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {

    private RabbitTemplate rabbitTemplate;

    private MessagePostProcessor messagePostProcessor;

    @Autowired
    public CommonProducer(RabbitTemplate rabbitTemplate, CommonMessagePostProcessor commonMessagePostProcessor) {
        this.rabbitTemplate = rabbitTemplate;
        messagePostProcessor = commonMessagePostProcessor;
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setUsePublisherConnection(true);
    }

    public void sendMessage(String exchangeName, String routingKey, Object message) {
        /**
         * convertAndSend() 异步方法
         * convertSendAndReceive() 同步方法，会等待消息被
         */
        rabbitTemplate.convertAndSend(exchangeName, routingKey, message, messagePostProcessor, new CorrelationData(UUID.randomUUID().toString()));
    }


    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if(ack) {
            log.info("message {} is confirmed", correlationData.getId());
        } else {
            log.warn("message {} is not confirmed, cause is {}", correlationData.getId(), cause);
        }
    }

    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        log.warn("message was returned!");
    }
}
