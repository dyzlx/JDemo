package com.dyz.demo.kafka.producer;

import com.dyz.demo.kafka.connection.ConnectionProperties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.dyz.demo.common.utils.CommonUtil.threadNum;

public class CommonProducer<K, V> {

    /**
     * 该对象是线程安全的，多个Producer线程其实可以共享该对象
     */
    private KafkaProducer<K, V> producer;

    /**
     *
     * @param keySerializerClass key的序列化器
     * @param valueSerializerClass value的序列化器
     */
    public CommonProducer(Class<?> keySerializerClass, Class<?> valueSerializerClass) {
        // 将消息Object转化为json字符串
        Properties connectionProperties = ConnectionProperties.getNewKafkaConnectionProperties();
        // 使用Kafka自带的String序列化器和反序列化器
        connectionProperties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializerClass.getName());
        connectionProperties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializerClass.getName());
        // 客户端ID
        connectionProperties.setProperty(ProducerConfig.CLIENT_ID_CONFIG, "kafka.demo.producer-1");
        // 设置重试和重试间隔时间，如果由于可重试异常导致发送消息失败，将进行重试
        connectionProperties.setProperty(ProducerConfig.RETRIES_CONFIG, "5");
        connectionProperties.setProperty(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, "2000");
        // 设置生产者拦截器
        connectionProperties.setProperty(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, CommonProducerInterceptor.class.getName());
        producer = new KafkaProducer<>(connectionProperties);
    }

    /**
     * 默认使用StringSerializer
     */
    public CommonProducer() {
        this(StringSerializer.class, StringSerializer.class);
    }

    /**
     * 同步的发送消息
     * @param messageKey 消息的Key，用于在没有指定分区时，计算分区
     * @param message 消息
     * @param topic 主题
     * @param partition 指定分区
     * @param timeStamp xx
     * @param headers 消息头
     */
    public void sendSync(K messageKey, V message,
                         String topic, Integer partition, Long timeStamp, Iterable<Header> headers) {
        if(Objects.isNull(message)) {
            return ;
        }
        System.out.println("[" + threadNum() + "]" + " sync send message " + message);
        // 构建消息
        ProducerRecord<K, V> record = new ProducerRecord<>(topic, partition, timeStamp, messageKey, message, headers);
        // 发送消息
        Future<RecordMetadata> future = producer.send(record);
        // 同步阻塞获取结果
        try {
            RecordMetadata recordMetadata = future.get();
            System.out.println("[" + threadNum() + "]" + " sync send message success," +
                    " to topic " + recordMetadata.topic() + ", to partition " +
                    recordMetadata.partition() + ", offset " + recordMetadata.offset());
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("[" + threadNum() + "]" + " sync send message fail");
            e.printStackTrace();
        }
    }

    /**
     * 异步的发送消息
     */
    public void sendAsync(K messageKey, V message,
                          String topic, Integer partition, Long timeStamp, Iterable<Header> headers) {
        if(Objects.isNull(message)) {
            return ;
        }
        System.out.println("[" + threadNum() + "]" + " async send message " + message);
        // 构建消息
        ProducerRecord<K, V> record = new ProducerRecord<>(topic, partition, timeStamp, messageKey, message, headers);
        // 异步发送消息
        producer.send(record, (recordMetadata, exception) -> {
            if (Objects.nonNull(exception)) {
                System.out.println("[" + threadNum() + "]" + " callback: async send message fail");
            } else {
                System.out.println("[" + threadNum() + "]" + " callback: async send message success," +
                        " to topic " + recordMetadata.topic() + ", to partition " +
                        recordMetadata.partition() + ", offset " + recordMetadata.offset());
            }
        });
    }

    public void close() {
        this.producer.close();
    }

//        final ObjectMapper objectMapper;
//        objectMapper = new ObjectMapper();
//        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        objectMapper.activateDefaultTyping(
//                LaissezFaireSubTypeValidator.instance,
//                ObjectMapper.DefaultTyping.NON_FINAL,
//                JsonTypeInfo.As.PROPERTY);
//        CommonProducer<String, String> commonProducer = new CommonProducer<>();
//        commonProducer.sendSync(null,
//                objectMapper.writeValueAsString(KafkaDemoMessage.builder().id(UUID.randomUUID().toString()).title("test").content("duyunze love lx").build()),
//                topic,
//                null, null, null);
//        commonProducer.sendAsync(null,
//                objectMapper.writeValueAsString(KafkaDemoMessage.builder().id(UUID.randomUUID().toString()).title("test").content("duyunze love lx").build()),
//                topic,
//                null, null, null);
//        commonProducer.close();
}
