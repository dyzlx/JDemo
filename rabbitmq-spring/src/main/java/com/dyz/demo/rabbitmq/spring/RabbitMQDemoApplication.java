package com.dyz.demo.rabbitmq.spring;

import com.dyz.demo.rabbitmq.spring.consumer.CommonConsumer;
import com.dyz.demo.rabbitmq.spring.model.CommonMessage;
import com.dyz.demo.rabbitmq.spring.producer.CommonProducer;
import com.dyz.demo.rabbitmq.spring.utils.SpringBeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.QueueInformation;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class RabbitMQDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(RabbitMQDemoApplication.class, args);

        CommonProducer producer = SpringBeanUtil.getBean(CommonProducer.class);
        producer.sendMessage("test_topic_exchange", "du.123", new CommonMessage(1, "dyz"));
        producer.sendMessage("test_topic_exchange", "du.123", new CommonMessage(2, "lx"));
        producer.sendMessage("test_topic_exchange", "du.123", new CommonMessage(3, "dyz.lx"));


        RabbitAdmin rabbitAdmin = SpringBeanUtil.getBean(RabbitAdmin.class);
        QueueInformation queueInformation = rabbitAdmin.getQueueInfo("test_queue_a");
        log.info("queue info: {}", queueInformation);
    }
}
