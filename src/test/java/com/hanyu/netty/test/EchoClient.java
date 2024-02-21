package com.hanyu.netty.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Scanner;

@Slf4j
public class EchoClient {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        ChannelFuture channelFuture = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();

                        pipeline.addLast(new StringEncoder());
                        pipeline.addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf = (ByteBuf) msg;
                                log.debug("服务器发送来的消息 {}", buf.toString(Charset.defaultCharset()));
                            }
                        });

                    }
                })
                .connect(new InetSocketAddress("localhost", 8080));
        Channel channel = channelFuture.sync().channel();

        new Thread(()->{
            Scanner scanner = new Scanner(System.in);
            while (true){
                String msg = scanner.next();
                if ("q".equals(msg)){
                    log.debug("正在关闭客户端");
                    channel.close();

                    break;
                }
                channel.writeAndFlush(msg);
            }
        },"h1").start();
        ChannelFuture closeFuture = channel.closeFuture();
        closeFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                log.debug("客户端关闭完成...");
                group.shutdownGracefully();
            }
        });
    }
}
