package com.dyz.demo.rabbitmq.spring.consumer;


import com.dyz.demo.rabbitmq.spring.model.CommonMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonMessageDelegate {

    public void handleMessage(String message) {
        log.info("message delegate get str message : {}", message);
    }

    public void handleMessage(CommonMessage message) {
        log.info("message delegate get message entuty : {}", message);
    }

}
