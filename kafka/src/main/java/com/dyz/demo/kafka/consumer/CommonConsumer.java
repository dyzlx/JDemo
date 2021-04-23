package com.dyz.demo.kafka.consumer;

import com.dyz.demo.kafka.connection.ConnectionProperties;
import com.dyz.demo.kafka.serializer.CustomizedJsonSerializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.dyz.demo.common.utils.CommonUtil.threadNum;

public class CommonConsumer<K, V> {

    protected KafkaConsumer<K, V> kafkaConsumer;

    private String consumerGroupId;

    protected AtomicBoolean isRunning  = new AtomicBoolean(true);

    public CommonConsumer(String consumerGroupId) {
        this(consumerGroupId, StringDeserializer.class, StringDeserializer.class);
    }

    public CommonConsumer(String consumerGroupId, Class<?> keyDeserializerClass, Class<?> valueDeserializerClass) {
        Properties connectionProperties = ConnectionProperties.getNewKafkaConnectionProperties();
        // 反序列化器
        connectionProperties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializerClass.getName());
        connectionProperties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializerClass.getName());
        // 客户端ID
        connectionProperties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, "kafka.demo.consumer-" + UUID.randomUUID().toString());
        // 消费者组ID
        connectionProperties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
        // 消费位移手动提交
        connectionProperties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        // 消费位移
        connectionProperties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        // 消费者拦截器
        connectionProperties.setProperty(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG, CommonConsumerInterceptor.class.getName());
        this.kafkaConsumer = new KafkaConsumer<>(connectionProperties);
    }

    /**
     * 从指定的主题中消费消息
     * @param topics 订阅的主题
     */
    public final void consume(Collection<String> topics) {
        // 订阅主题，并配置再均衡监听器
        this.kafkaConsumer.subscribe(topics, getConsumerRebalanceListener());
        doConsume();
    }

    /**
     * 从指定的主题分区中消费消息
     * 通过assign方法订阅分区，如法进行消费者再均衡
     * @param topicPartitions 订阅的主题-分区
     */
    public final void consume(Map<String, Integer> topicPartitions) {
        Set<TopicPartition> tps = new HashSet<>();
        for(Map.Entry<String, Integer> entry : topicPartitions.entrySet()) {
            tps.add(new TopicPartition(entry.getKey(), entry.getValue()));
        }
        this.kafkaConsumer.assign(tps);
        doConsume();
    }

    /**
     * 按照主题-分区的维度进行消费，并手动同步提交消费位移
     */
    protected void doConsume() {
        try {
            while(isRunning.get()) {
                ConsumerRecords<K, V> records = this.kafkaConsumer.poll(Duration.ofMillis(2000));
                System.out.println(threadNum() + "poll message from kafka broker, count = " + records.count());
                Set<TopicPartition> tps = records.partitions();
                for(TopicPartition tp : tps) {
                    for(ConsumerRecord<K, V> record : records.records(tp)) {
                        System.out.println(threadNum() + " consume message " + record.value() + " from topic " + record.topic() +" partition " + record.partition());
                    }
                    // 先处理消息在提交消费位移，防止消息丢失
                    System.out.println(threadNum() + "commit consume offsets for partition: " + tp);
                    this.kafkaConsumer.commitSync();
                }
            }
        } catch (WakeupException wakeupException) {
            System.out.println(threadNum() + "consumer wake up!");
        } catch (Exception e) {
            System.out.println(threadNum() + "consume error! " + e.getMessage());
        } finally {
            // 最后的把关，防止重复消费
            this.kafkaConsumer.commitSync();
        }
    }

    /**
     * 消费者Rebalance的回调逻辑
     * @return
     */
    protected ConsumerRebalanceListener getConsumerRebalanceListener() {
        return new ConsumerRebalanceListener() {
            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                System.out.println(threadNum() + "[ConsumerRebalanceListener] current consumer will stop polling message from those partition: " + partitions);
            }
            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                System.out.println(threadNum() + "[ConsumerRebalanceListener] current consumer will start polling message from those partition: " + partitions);
            }
        };
    }

    public final void wakeUp() {
        System.out.println(threadNum() + "consumer wakeup!");
        this.kafkaConsumer.wakeup();
        //this.isRunning.set(false);
    }

    /**
     * 暂停某个分区的消费
     * @param tp 主题-分区
     */
    public final void pause(Collection<TopicPartition> tp) {
        this.kafkaConsumer.pause(tp);
    }

    /**
     * 暂停全部分区的消费
     */
    public final void pause() {
        pause(this.kafkaConsumer.assignment());
    }

    /**
     * 恢复某个分区的消费
     * @param tp
     */
    public final void resume(Collection<TopicPartition> tp) {
        this.kafkaConsumer.resume(tp);
    }

    /**
     * 恢复全部分区的消费
     */
    public final void resume() {
        resume(this.kafkaConsumer.assignment());
    }
}
