package com.ruoyi.websocket.nettyServer;

import java.nio.channels.Channel;
import java.util.Map;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

public abstract class NettyWebSocketEndpointHandler {

   private final ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

   private Map<String, String> pathParam;

   private Map<String, String> urlParam;

   public void sendAll(String msg) {
      group.writeAndFlush(msg);
   }

   public static void sendMsg(ChannelHandlerContext context, String msg) {
      TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame(msg);
      context.channel().writeAndFlush(textWebSocketFrame);
   }

   public abstract void onMessage(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame);

   public abstract void onOpen(ChannelHandlerContext channelHandlerContext, FullHttpMessage fullHttpMessage);

   public abstract void onClose(ChannelHandlerContext channelHandlerContext);

   public abstract void onError(ChannelHandlerContext channelHandlerContext, Throwable throwable);

   public ChannelGroup getGroup() {
      return group;
   }

   public Map<String, String> getPathParam() {
      return pathParam;
   }

   public void setPathParam(Map<String, String> pathParam) {
      this.pathParam = pathParam;
   }

   public Map<String, String> getUrlParam() {
      return urlParam;
   }

   public void setUrlParam(Map<String, String> urlParam) {
      this.urlParam = urlParam;
   }

   public Long getLongPathParam(String key) {
      return Long.valueOf(pathParam.get(key));
   }

   public String getPathParam(String key) {
      return pathParam.get(key);
   }

   public Double getDoublePathParam(String key) {
      return Double.parseDouble(pathParam.get(key));
   }

   public void closeChannel(ChannelHandlerContext channelHandlerContext) {
      channelHandlerContext.close().addListener(ChannelFutureListener.CLOSE);
   }
}