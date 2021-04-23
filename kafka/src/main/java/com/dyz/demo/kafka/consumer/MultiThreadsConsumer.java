package com.dyz.demo.kafka.consumer;

import org.apache.commons.collections.MapUtils;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;

import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.dyz.demo.common.utils.CommonUtil.threadNum;

/**
 * 使用多线程处理消息的消费者实现
 * 注意：
 * 1）保证消息消费的顺序，不能乱序处理
 * 2）消费位移的提交
 */
public class MultiThreadsConsumer<K,V> extends CommonConsumer<K,V> {

    private Map<TopicPartition, ExecutorService> partitionExecutorMap = new HashMap<>();

    public MultiThreadsConsumer(String consumerGroupId) {
        super(consumerGroupId);
    }

    public MultiThreadsConsumer(String consumerGroupId, Class<?> keyDeserializerClass, Class<?> valueDeserializerClass) {
        super(consumerGroupId, keyDeserializerClass, valueDeserializerClass);
    }

    @Override
    protected void doConsume() {
        try {
            while(isRunning.get()) {
                ConsumerRecords<K, V> records = this.kafkaConsumer.poll(Duration.ofMillis(2000));
                System.out.println(threadNum() + "poll message from kafka broker, count = " + records.count());
                Set<TopicPartition> tps = records.partitions();
                // 第一次poll后根据分区数初始化线程池
                if(MapUtils.isEmpty(partitionExecutorMap)) {
                    initPartitionExecutorMap(tps);
                }
                // 每个分区的消息交给对应的SingleThreadExecutor处理
                for(TopicPartition tp : tps) {
                    List<ConsumerRecord<K,V>> tpRecords = records.records(tp);
                    partitionExecutorMap.get(tp).execute(() -> {
                        for(ConsumerRecord<K, V> record : tpRecords) {
                            // 处理消息
                            System.out.println(threadNum() + " consume message " + record.value() +
                                    " from topic " + record.topic() +" partition " + record.partition());
                        }
                    });
                    // 这样提交offset容易造成消息丢失（因为提交的时候还不知道消息是否已经真正处理完）
                    this.kafkaConsumer.commitSync();
                }
            }
        } catch (WakeupException wakeupException) {
            System.out.println(threadNum() + "consumer wake up!");
        } catch (Exception e) {
            System.out.println(threadNum() + "consume error! " + e.getMessage());
        } finally {

        }
    }

    @Override
    protected ConsumerRebalanceListener getConsumerRebalanceListener() {
        return new ConsumerRebalanceListener() {
            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                System.out.println(threadNum() + "[ConsumerRebalanceListener] current consumer will stop polling message from those partition: " + partitions);
            }
            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                // 重新设置线程池的数量
                initPartitionExecutorMap(new HashSet<>(partitions));
            }
        };
    }

    private void initPartitionExecutorMap(Set<TopicPartition> tps) {
        if(Objects.isNull(partitionExecutorMap)) {
            partitionExecutorMap = new HashMap<>();
        }
        partitionExecutorMap.clear();
        for(TopicPartition tp : tps) {
            partitionExecutorMap.put(tp, Executors.newSingleThreadExecutor());
        }
    }
}
