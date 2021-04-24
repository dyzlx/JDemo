package com.dyz.demo.kafka;

import com.dyz.demo.kafka.consumer.CommonConsumer;
import com.dyz.demo.kafka.consumer.MultiThreadsConsumer;
import com.dyz.demo.kafka.model.KafkaDemoMessage;
import com.dyz.demo.kafka.producer.CommonProducer;
import com.dyz.demo.kafka.serializer.CustomizedJsonSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Collections;
import java.util.UUID;

public class KafkaDemoApp {

    private static final String TOPIC = "dyz-lx";

    private static final String CONSUMER_GROUP = "DL-C-1";

    public static void main(String[] args) throws InterruptedException {
        // 另一个线程消费消息
        CommonConsumer<String, KafkaDemoMessage> commonConsumer
                = new MultiThreadsConsumer<>(CONSUMER_GROUP, StringDeserializer.class, CustomizedJsonSerializer.JsonDeserializer.class);
        new Thread(() -> {
            commonConsumer.consume(Collections.singletonList(TOPIC));
        }, "Demo-Consumer").start();

        // sleep等待消费者上线
        Thread.sleep(5000);

        // 生产消息
        CommonProducer<String, KafkaDemoMessage> commonProducer
                = new CommonProducer<>(StringSerializer.class, CustomizedJsonSerializer.JsonSerializer.class);
        for(int i = 0 ; i < 20 ; i++) {
            commonProducer.sendSync(String.valueOf(i),
                    KafkaDemoMessage.builder().id(UUID.randomUUID().toString()).title("test").content("duyunze love lx").build(),
                    TOPIC,
                    null, null, null);
        }
        commonProducer.close();
    }
}
