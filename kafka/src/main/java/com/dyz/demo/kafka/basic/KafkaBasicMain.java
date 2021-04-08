package com.dyz.demo.kafka.basic;

import com.dyz.demo.kafka.connection.ConnectionProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import static com.dyz.demo.common.utils.CommonUtil.threadNum;

public class KafkaBasicMain {
    public static void main(String[] args) throws InterruptedException, JsonProcessingException {
        final String topic = "test";
        Properties properties = ConnectionProperties.getNewKafkaConnectionProperties();
        properties.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.setProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        // 消息生产者
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);
        // 消息消费者
        final String consumerGroupId = "dyz-demo";
        properties.setProperty("group.id", consumerGroupId); // 设置消费者组
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Collections.singletonList(topic)); // 订阅主题 // 一个消费者可以订阅多个主题

        // 起一个线程，循环消费消息
        new Thread(() -> {
            while(true) {
                // 在1s的时间段内拉取消息，可能拉取到多个topic的多个消息
                ConsumerRecords<String, String> messageList = consumer.poll(Duration.ofMillis(1000));
                for(ConsumerRecord<String, String> message : messageList) {
                    System.out.println("[" + threadNum() + "]" + " consume message " + message.value() + " from topic " + message.topic() +" partition " + message.partition());
                }
                // 睡一下，降低CPU
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "consumer").start();

        // 发送5条消息
        for (int i = 0 ; i < 5 ; i++) {
            // 构建消息
            ProducerRecord<String, String> message = new ProducerRecord<>(topic, "hello-" + i);
            // 发送消息
            try {
                System.out.println("[" + threadNum() + "]" + " producer send message " + message.value() + " from topic " + message.topic() +" partition " + message.partition());
                producer.send(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 睡2秒发一条
            Thread.sleep(2000);
        }
        producer.close();
    }
}
