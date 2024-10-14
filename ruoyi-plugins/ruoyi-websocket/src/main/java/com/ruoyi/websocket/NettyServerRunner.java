package com.ruoyi.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.web.embedded.netty.NettyWebServer;
import org.springframework.stereotype.Component;

import com.ruoyi.websocket.nettyServer.NettyWebSocketServer;

@Component
public class NettyServerRunner implements ApplicationRunner {

   @Autowired
   private NettyWebSocketServer server;

   @Override
   public void run(ApplicationArguments args) throws Exception {
      server.start();
   }

}
