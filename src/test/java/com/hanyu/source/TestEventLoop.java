package com.hanyu.source;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;

public class TestEventLoop {
    public static void main(String[] args) {
        EventLoop eventLoop = new NioEventLoopGroup().next();
        eventLoop.execute(()->{
            System.out.println("hello");
        });
    }
}
