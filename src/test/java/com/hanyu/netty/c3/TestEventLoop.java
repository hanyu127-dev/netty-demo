package com.hanyu.netty.c3;

import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.NettyRuntime;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class TestEventLoop {


    public static void main(String[] args) {
        // 1. 创建事件循环组
        EventLoopGroup group = new NioEventLoopGroup();  // io 事件， 普通任务， 定时任务 2*8
//        EventLoopGroup group = new DefaultEventLoopGroup(); //  普通任务， 定时任务
//        System.out.println(NettyRuntime.availableProcessors());
        // 2. 获取下一个事件循环对象

        System.out.println(group.next());
        System.out.println(group.next());
        System.out.println(group.next());

        // 3. 执行普通任务
//        group.next().submit(()->{
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            log.debug("ok");
//        });

        // 4. 执行定时任务
        group.next().scheduleAtFixedRate(()->{
            log.debug("pk");
        },0,1, TimeUnit.SECONDS);



    }
}
