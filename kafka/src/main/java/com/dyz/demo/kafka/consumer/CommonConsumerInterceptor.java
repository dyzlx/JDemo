package com.dyz.demo.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.util.Map;

import static com.dyz.demo.common.utils.CommonUtil.threadNum;

public class CommonConsumerInterceptor<K, V> implements ConsumerInterceptor<K, V> {

    @Override
    public ConsumerRecords<K, V> onConsume(ConsumerRecords<K, V> records) {
        System.out.println(threadNum() + "[ConsumerInterceptor] onConsume");
        return records;
    }

    @Override
    public void onCommit(Map offsets) {
        System.out.println(threadNum() + "[ConsumerInterceptor] onCommit, committed offsets = " + offsets);
    }

    @Override
    public void configure(Map<String, ?> configs) {

    }

    @Override
    public void close() {
        System.out.println(threadNum() + "[ConsumerInterceptor] close interceptor");
    }
}
