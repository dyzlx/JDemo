package com.dyz.demo.rabbitmq.spring.config;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class AMQPComponentConfiguration {

    @Autowired
    private CachingConnectionFactory cachingConnectionFactory;

    @Bean
    public RabbitAdmin rabbitAdmin() {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(cachingConnectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

        @Bean
        public Queue testQueueA() {
            Map<String, Object> args = new HashMap<>();
            return new Queue("test_queue_a", false, false, true, args);
        }

    @Bean
    public Queue testQueueB() {
        Map<String, Object> args = new HashMap<>();
        return new Queue("test_queue_b", false, false, true, args);
    }

    @Bean
    public Queue testQueueC() {
        Map<String, Object> args = new HashMap<>();
        return new Queue("test_queue_c", false, false, true, args);
    }

    @Bean
    public TopicExchange testTopicExchange() {
        Map<String, Object> args = new HashMap<>();
        return new TopicExchange("test_topic_exchange", false, false, args);
    }

//    @Bean
//    public FanoutExchange testFanoutExchange() {
//        Map<String, Object> args = new HashMap<>();
//        return new FanoutExchange("test_fanout_exchange", false, false, args);
//    }
//
//    @Bean
//    public DirectExchange testDirectExchange() {
//        Map<String, Object> args = new HashMap<>();
//        return new DirectExchange("test_direct_exchange", false, false, args);
//    }

    @Bean
    public Binding testBindA(Queue testQueueA, Exchange testTopicExchange) {
        return BindingBuilder.bind(testQueueA).to(testTopicExchange).with("du.*").noargs();
    }

    @Bean
    public Binding testBindB(Queue testQueueB, Exchange testTopicExchange) {
        return BindingBuilder.bind(testQueueB).to(testTopicExchange).with("yun.*").noargs();
    }

    @Bean
    public Binding testBindC(Queue testQueueC, Exchange testTopicExchange) {
        return BindingBuilder.bind(testQueueC).to(testTopicExchange).with("ze.*").noargs();
    }
}
