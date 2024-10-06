package com.ruoyi.middleware.rabbitmq;

import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "TestDirectQueue") // 监听的队列名称 TestDirectQueue
public class DirectReceiver {

    @RabbitHandler
    public void process(Map map) {
        System.out.println("DirectReceiver m消费者收到消息  : " + map.toString());
    }

    @RabbitHandler
    public void process(String map) {
        System.out.println("DirectReceiver s消费者收到消息  : " + map.toString());
    }

    @RabbitHandler
    public void process(Byte[] b) {
        System.out.println("DirectReceiver s消费者收到消息  : " + b.toString());
    }

}