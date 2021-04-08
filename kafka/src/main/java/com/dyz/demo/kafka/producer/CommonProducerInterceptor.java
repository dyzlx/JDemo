package com.dyz.demo.kafka.producer;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Map;

import static com.dyz.demo.common.utils.CommonUtil.threadNum;

public class CommonProducerInterceptor<K, V> implements ProducerInterceptor<K, V> {

    /**
     * 在消息发送前执行
     * @param record
     * @return
     */
    @Override
    public ProducerRecord<K, V> onSend(ProducerRecord<K, V> record) {
        System.out.println("[" + threadNum() + "]" + "[producer interceptor] before send message");
        return record;
    }

    /**
     * 在消息应答前或者消息发送失败时执行（先与callback执行）
     * @param metadata
     * @param exception
     */
    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
        System.out.println("[" + threadNum() + "]" + "[producer interceptor] before ack message" + ", exception=" + exception);
    }

    /**
     * 在关闭拦截器前执行
     */
    @Override
    public void close() {
        System.out.println("[" + threadNum() + "]" + "[producer interceptor] close interceptor");
    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
