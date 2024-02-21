package com.hanyu.netty.c3;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventLoopClient1 {
    public static void main(String[] args) throws InterruptedException {
        ChannelFuture channelFuture = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect("127.0.0.1", 8080);

        log.debug("{}",channelFuture.channel());
        System.out.println("===========");

        // 使用sync 方法同步处理结果
//        channelFuture.sync(); // 2

        // 使用addListener 异步处理结果
        channelFuture.addListener(new ChannelFutureListener() {
            // nio 连接建立好了
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                Channel channel = channelFuture.channel();
                log.debug("{}",channel);
                channel.writeAndFlush("hello world");
            }
        });

    }
}
