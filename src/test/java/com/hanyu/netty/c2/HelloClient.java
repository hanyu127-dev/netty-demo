package com.hanyu.netty.c2;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

public class HelloClient {
    public static void main(String[] args) throws InterruptedException {
        // 1. 启动类
        new Bootstrap()
                // 2. 添加EventLoop
                .group(new NioEventLoopGroup())
                // 3. 选择客户端 channel
                .channel(NioSocketChannel.class)
                // 4.添加处理器
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    // 在连接建立后被调用
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        // 将字符串 转为 ByteBuf
                        ch.pipeline().addLast(new StringEncoder());

                    }
                })
                // 5.连接服务器
                .connect(new InetSocketAddress("localhost",8080))
                // 6. 阻塞方法，直到连接建立
                .sync()
                 // 7. 客户端与服务器端的连接对象 channel
                .channel()
                // 向服务器发送数据
                .writeAndFlush("hello ,world");
    }
}
