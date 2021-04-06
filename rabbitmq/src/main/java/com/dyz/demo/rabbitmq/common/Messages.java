package com.dyz.demo.rabbitmq.common;

import com.rabbitmq.client.MessageProperties;

import java.util.ArrayList;
import java.util.List;

public class Messages {

    private static List<CommonMessage> LIST = new ArrayList<>();

    private final static String ROUTING_KEY1 = "du.test";

    private final static String ROUTING_KEY2 = "yun.test";

    private final static String ROUTING_KEY3 = "lx.test";

    static {
        //=======================================ROUTING_KEY1====================================
        LIST.add(CommonMessage.builder()
                .content("DYZ-10-LX")
                .routingKey(ROUTING_KEY1)
                .basicProperties(MessageProperties.TEXT_PLAIN.builder()
                        .build())
                .build());

        LIST.add(CommonMessage.builder()
                .content("DYZ-09-LX")
                .routingKey(ROUTING_KEY1)
                .build());

        LIST.add(CommonMessage.builder()
                .content("DYZ-08-LX")
                .routingKey(ROUTING_KEY1)
                .build());

        LIST.add(CommonMessage.builder()
                .content("DYZ-07-LX")
                .routingKey(ROUTING_KEY1)
                .build());

        LIST.add(CommonMessage.builder()
                .content("DYZ-06-LX")
                .routingKey(ROUTING_KEY1)
                .build());

        LIST.add(CommonMessage.builder()
                .content("DYZ-05-LX")
                .routingKey(ROUTING_KEY1)
                .build());

        LIST.add(CommonMessage.builder()
                .content("DYZ-04-LX")
                .routingKey(ROUTING_KEY1)
                .build());

        LIST.add(CommonMessage.builder()
                .content("DYZ-03-LX")
                .routingKey(ROUTING_KEY1)
                .build());

        LIST.add(CommonMessage.builder()
                .content("DYZ-02-LX")
                .routingKey(ROUTING_KEY1)
                .build());

        LIST.add(CommonMessage.builder()
                .content("DYZ-01-LX")
                .routingKey(ROUTING_KEY1)
                .build());

        //=======================================ROUTING_KEY2====================================
//        LIST.add(CommonMessage.builder()
//                .content("DYZ-11-LX")
//                .routingKey(ROUTING_KEY2)
//                .basicProperties(MessageProperties.TEXT_PLAIN.builder()
//                        .build())
//                .build());
//
//        LIST.add(CommonMessage.builder()
//                .content("DYZ-12-LX")
//                .routingKey(ROUTING_KEY2)
//                .build());
//
//        LIST.add(CommonMessage.builder()
//                .content("DYZ-13-LX")
//                .routingKey(ROUTING_KEY2)
//                .build());
//
//        LIST.add(CommonMessage.builder()
//                .content("DYZ-14-LX")
//                .routingKey(ROUTING_KEY2)
//                .build());
//
//        LIST.add(CommonMessage.builder()
//                .content("DYZ-15-LX")
//                .routingKey(ROUTING_KEY2)
//                .build());
//
//        LIST.add(CommonMessage.builder()
//                .content("DYZ-16-LX")
//                .routingKey(ROUTING_KEY2)
//                .build());
//
//        LIST.add(CommonMessage.builder()
//                .content("DYZ-17-LX")
//                .routingKey(ROUTING_KEY2)
//                .build());
//
//        LIST.add(CommonMessage.builder()
//                .content("DYZ-18-LX")
//                .routingKey(ROUTING_KEY2)
//                .build());
//
//        LIST.add(CommonMessage.builder()
//                .content("DYZ-19-LX")
//                .routingKey(ROUTING_KEY2)
//                .build());
//
//        LIST.add(CommonMessage.builder()
//                .content("DYZ-20-LX")
//                .routingKey(ROUTING_KEY2)
//                .build());
    }

    public static List<CommonMessage> get() {
        return LIST;
    }
}
