package com.ruoyi.netty.websocket.nettyServer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ruoyi.netty.websocket.nettyServer.handler.WebSocketHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

@Component
public class NettyWebSocketServer {

   private static ServerBootstrap serverBootstrap;

   @Value("${netty.websocket.maxMessageSize}")
   private Long messageSize;

   @Value("${netty.websocket.bossThreads}")
   private Long bossThreads;

   @Value("${netty.websocket.workerThreads}")
   private Long workerThreads;

   @Value("${netty.websocket.port}")
   private Long port;

   @Value("${netty.websocket.enable}")
   private Boolean enable;

   public ServerBootstrap start() throws InterruptedException {
      if (!enable) {
         return null;
      }
      ServerBootstrap serverBootstrap = new ServerBootstrap();
      NioEventLoopGroup boss = new NioEventLoopGroup(4);
      NioEventLoopGroup worker = new NioEventLoopGroup(workerThreads.intValue());
      serverBootstrap.group(boss, worker);
      serverBootstrap.channel(NioServerSocketChannel.class);
      serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
      serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
         @Override
         protected void initChannel(NioSocketChannel channel) throws Exception {
            ChannelPipeline pipeline = channel.pipeline();
            pipeline.addLast(new HttpServerCodec());
            pipeline.addLast(new HttpObjectAggregator(messageSize.intValue()));
            pipeline.addLast(new WebSocketHandler());
            pipeline.addLast(new WebSocketServerProtocolHandler("/", true));
         }
      });
      serverBootstrap.bind(port.intValue()).sync();
      System.out.println(
            "----------------------------------------------------------------------------------- \n Arknights!");
      NettyWebSocketServer.serverBootstrap = serverBootstrap;
      return NettyWebSocketServer.serverBootstrap;
   }
}
