package com.dyz.demo.kafka.serializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class CustomizedJsonSerializer {

    public static class JsonSerializer implements Serializer<Object> {

        private final ObjectMapper objectMapper;

        public JsonSerializer() {
            this.objectMapper = new ObjectMapper();
            objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
            objectMapper.activateDefaultTyping(
                    LaissezFaireSubTypeValidator.instance,
                    ObjectMapper.DefaultTyping.NON_FINAL,
                    JsonTypeInfo.As.PROPERTY);
        }

        @Override
        public void configure(Map configs, boolean isKey) {
        }

        @Override
        public byte[] serialize(String topic, Object data) {
            if(data == null) {
                return new byte[0];
            }
            try {
                return objectMapper.writeValueAsBytes(data);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static class JsonDeserializer implements Deserializer<Object> {

        private final ObjectMapper objectMapper;

        public JsonDeserializer() {
            this.objectMapper = new ObjectMapper();
            objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
            objectMapper.activateDefaultTyping(
                    LaissezFaireSubTypeValidator.instance,
                    ObjectMapper.DefaultTyping.NON_FINAL,
                    JsonTypeInfo.As.PROPERTY);
        }

        @Override
        public void configure(Map configs, boolean isKey) {
        }

        @Override
        public Object deserialize(String topic, byte[] data) {
            if(data == null || data.length == 0) {
                return null;
            }
            try {
                return objectMapper.readValue(data, Object.class);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
