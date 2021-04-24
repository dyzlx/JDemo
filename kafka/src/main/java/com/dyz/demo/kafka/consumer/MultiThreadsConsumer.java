package com.dyz.demo.kafka.consumer;

import org.apache.commons.collections.MapUtils;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.dyz.demo.common.utils.CommonUtil.threadNum;

/**
 * 使用多线程处理消息的消费者实现
 * 注意：
 * 1）保证消息消费的顺序，不能乱序处理
 * 2）消费位移的提交
 */
public class MultiThreadsConsumer<K,V> extends CommonConsumer<K,V> {

    private Map<TopicPartition, ExecutorService> partitionExecutorMap = new HashMap<>();

    Map<TopicPartition, OffsetAndMetadata> offsets = new ConcurrentHashMap<>();

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
                // 这次要提交的位移, 可能多线程操作
                ConsumerRecords<K, V> records = this.kafkaConsumer.poll(Duration.ofMillis(2000));
                System.out.println(threadNum() + "poll message from kafka broker, count = " + records.count());
                Set<TopicPartition> tps = records.partitions();
                // 第一次poll后根据分区数初始化线程池以及cyclicBarrier
                if (MapUtils.isEmpty(partitionExecutorMap)) {
                    initPartitionExecutorMap(tps);
                }
                CountDownLatch countDownLatch = new CountDownLatch(tps.size());
                // 每个分区的消息交给对应的SingleThreadExecutor处理
                for(TopicPartition tp : tps) {
                    List<ConsumerRecord<K,V>> tpRecords = records.records(tp);
                    partitionExecutorMap.get(tp).execute(new RecordsHandler<>(tp, tpRecords, offsets, countDownLatch));
                }
                // 等待多个线程处理结束
                if(!countDownLatch.await(10000, TimeUnit.MILLISECONDS)) {
                    System.out.println("[Error] countDownLatch await timeout!");
                    // 做一些失败处理，目前实现为 不提交offset
                    continue;
                }
                this.kafkaConsumer.commitSync(offsets);
                System.out.println(threadNum() + "commit offsets " + offsets);
                offsets.clear();
            }
        } catch (WakeupException wakeupException) {
            System.out.println("[Error]" + threadNum() + "consumer wake up!");
        } catch (Exception e) {
            System.out.println("[Error]" + threadNum() + "consume error! " + e.getMessage());
        } finally {
            // 最后的把关，防止重复消费
            this.kafkaConsumer.commitSync();
        }
    }

    @Override
    protected ConsumerRebalanceListener getConsumerRebalanceListener() {
        return new ConsumerRebalanceListener() {
            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                System.out.println(threadNum() + "[ConsumerRebalanceListener]" +
                        " current consumer will stop polling message from those partition: " + partitions);
            }
            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                System.out.println(threadNum() + "[ConsumerRebalanceListener]" +
                        " current consumer will start polling message from those partition: " + partitions);
                // 重新设置线程池的数量
                initPartitionExecutorMap(new HashSet<>(partitions));
                System.out.println(threadNum() + "[ConsumerRebalanceListener]" +
                        " reset partition executor number to " + partitions.size());
            }
        };
    }

    private void initPartitionExecutorMap(Set<TopicPartition> tps) {
        if(Objects.isNull(partitionExecutorMap)) {
            partitionExecutorMap = new HashMap<>();
        }
        partitionExecutorMap.clear();
        for(TopicPartition tp : tps) {
            partitionExecutorMap.put(tp, Executors.newSingleThreadExecutor(new ConsumerThreadFactory(tp.toString())));
        }
    }

    private static class RecordsHandler<K,V> extends Thread{
        private TopicPartition tp;
        private List<ConsumerRecord<K,V>> records;
        private Map<TopicPartition, OffsetAndMetadata> offsets;
        private CountDownLatch countDownLatch;
        public RecordsHandler(TopicPartition tp, List<ConsumerRecord<K,V>> records,
                              Map<TopicPartition, OffsetAndMetadata> offsets, CountDownLatch countDownLatch) {
            this.tp = tp;
            this.records = records;
            this.offsets = offsets;
            this.countDownLatch = countDownLatch;
        }
        @Override
        public void run() {
            for(ConsumerRecord<K, V> record : records) {
                // 处理消息
                System.out.println(threadNum() + " consume message key=" + record.key() + " value=" + record.value() +
                        " from topic " + record.topic() +" partition " + record.partition());
                // 添加消费位移
                long lastConsumedOffset = records.get(records.size() - 1).offset();
                offsets.put(tp, new OffsetAndMetadata(lastConsumedOffset + 1));
                countDownLatch.countDown();
            }
        }
    }

    private static class ConsumerThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        ConsumerThreadFactory(String slot) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "pool-" +
                    poolNumber.getAndIncrement() + "-" + slot +
                    "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}
