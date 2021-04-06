package com.dyz.demo.rabbitmq.spring.config;

import com.dyz.demo.rabbitmq.spring.consumer.CommonMessageDelegate;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

// https://www.jianshu.com/p/666961010646
//@Configuration
public class SimpleMessageListenerConfiguration {

    @Autowired
    private CachingConnectionFactory cachingConnectionFactory;

    @Autowired
    private Queue testQueueA;

    @Autowired
    private Queue testQueueB;

    @Autowired
    private Queue testQueueC;

    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer() {
        SimpleMessageListenerContainer messageListenerContainer = new SimpleMessageListenerContainer(cachingConnectionFactory);
        messageListenerContainer.setQueues(testQueueA, testQueueB, testQueueC);
        messageListenerContainer.setConcurrentConsumers(2);
        messageListenerContainer.setMaxConcurrentConsumers(4);
        messageListenerContainer.setDefaultRequeueRejected(false);
        messageListenerContainer.setAcknowledgeMode(AcknowledgeMode.AUTO);
        messageListenerContainer.setConsumerTagStrategy(x -> x + "_" + UUID.randomUUID().toString());
//        messageListenerContainer.setMessageListener(new ChannelAwareMessageListener(){
//            @Override
//            public void onMessage(Message message, Channel channel) throws Exception {
//                String str = new String(message.getBody());
//                System.out.println("listener get message : " + str);
//            }
//        });
        MessageListenerAdapter adapter = new MessageListenerAdapter(new CommonMessageDelegate());
        adapter.setMessageConverter(new Jackson2JsonMessageConverter());
        messageListenerContainer.setMessageListener(adapter);
        return messageListenerContainer;
    }
}
