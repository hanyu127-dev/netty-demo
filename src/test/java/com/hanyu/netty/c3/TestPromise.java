package com.hanyu.netty.c3;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import static com.google.common.collect.ComparisonChain.start;
@Slf4j
public class TestPromise {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 1. eventloop对象
        EventLoop eventLoop = new NioEventLoopGroup().next();



        // 2.可以主动创建Promise 对象, 结果的容器
        DefaultPromise<Integer> promise = new DefaultPromise<>(eventLoop);

        new Thread(()->{
            // 3. 任意线程执行计算，计算后向promise 填充结果
            log.debug("开始计算");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            promise.setSuccess(80);
        }).start();

        // 2. 接收结果
        log.debug("等待结果");
        log.debug("结果是{}",promise.get());

    }
}
