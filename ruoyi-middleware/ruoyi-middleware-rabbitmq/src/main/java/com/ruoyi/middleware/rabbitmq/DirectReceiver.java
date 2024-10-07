package com.ruoyi.middleware.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ruoyi.common.core.domain.Message;

@Component
@RabbitListener(queues = "TestDirectQueue") // 监听的队列名称 TestDirectQueue
public class DirectReceiver {

    @RabbitHandler
    public void process(Message map) {
        System.out.println("DirectReceiver m消费者收到消息  : " + map.toString());
    }
}