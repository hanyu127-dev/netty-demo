package com.hanyu.netty.c3;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Scanner;
@Slf4j
public class CloseFutureClient {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        ChannelFuture channelFuture = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        nioSocketChannel.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect(new InetSocketAddress("127.0.0.1", 8080));

        Channel channel = channelFuture.sync().channel();
        log.debug("{}",channel);
        new Thread(()->{
            Scanner scanner = new Scanner(System.in);
            while (true){
                String line = scanner.next();
                if ("q".equals(line)){
                    channel.close();
//                    log.debug("处理关闭之后的操作");
                    break;
                }
                channel.writeAndFlush(line);
            }
        },"input").start();

        // 获取 Future 对象
        ChannelFuture closeFuture = channel.closeFuture();
//        log.debug("waiting close ...");
//        closeFuture.sync();
        closeFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                log.debug(" 处理关闭操作完成之后的操作");
                group.shutdownGracefully();
            }
        });



    }
}
