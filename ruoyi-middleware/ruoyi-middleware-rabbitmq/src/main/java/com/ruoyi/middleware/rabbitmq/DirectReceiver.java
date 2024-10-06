package com.ruoyi.middleware.rabbitmq;

import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "spring.rabbitmq", name = { "enable" }, havingValue = "true", matchIfMissing = false)
@RabbitListener(queues = "TestDirectQueue") // 监听的队列名称 TestDirectQueue
public class DirectReceiver {

    @RabbitHandler
    public void process(Map map) {
        System.out.println("DirectReceiver消费者收到消息  : " + map.toString());
    }

}