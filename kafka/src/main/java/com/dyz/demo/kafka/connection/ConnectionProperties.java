package com.dyz.demo.kafka.connection;

import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.Properties;

public class ConnectionProperties {

    public static final String brokerList = "192.168.1.153:9092";

    public static Properties getNewKafkaConnectionProperties() {
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        return properties;
    }
}
