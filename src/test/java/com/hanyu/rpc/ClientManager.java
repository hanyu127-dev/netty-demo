package com.hanyu.rpc;

import com.hanyu.message.RpcRequestMessage;
import com.hanyu.protocol.MessageCodecSharable;
import com.hanyu.protocol.ProtocolFrameDecoder;
import com.hanyu.protocol.SequenceIdGenerator;
import com.hanyu.server.handler.RpcResponseMessageHandler;
import com.hanyu.server.service.HelloService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Slf4j
public class ClientManager {
    private static volatile Channel channel = null;
    private static final Object LOCK = new Object();


    public static void main(String[] args) {
        HelloService service = getProxyService(HelloService.class);

        System.out.println(service.sayHello("张三"));
    }

    // 创建代理类
    public static <T> T getProxyService(Class<T> serviceClass){
        ClassLoader loader = serviceClass.getClassLoader();
        Class<?>[] interfaces = new Class[]{serviceClass};
        Object o = Proxy.newProxyInstance(loader, interfaces, (proxy, method, args) -> {
            int id = SequenceIdGenerator.nextId();
            // 将方法调用转换未 消息对象
            RpcRequestMessage message = new RpcRequestMessage(
                   id,
                    serviceClass.getName(),
                    method.getName(),
                    method.getReturnType(),
                    method.getParameterTypes(),
                    args
            );
            // 发消息
            getChannel().writeAndFlush(message);

            // 准备空书包 promise  ,指定promise异步接收结果的线程
            DefaultPromise<Object> promise = new DefaultPromise<>(getChannel().eventLoop());
            RpcResponseMessageHandler.PROMISE_MAP.put(id,promise);

            // 等待promise的结果
            promise.await();

            if (promise.isSuccess()) {
                return promise.getNow();
            }
            // 失败了
            return new RuntimeException(promise.cause());
        });
        return (T) o;
    }
    // 单例模式  双重检查
    public static Channel getChannel(){
        if (channel!=null){
            return channel;
        }
        synchronized (LOCK){
            if (channel!=null){
                return channel;
            }
            initChannel();
            return channel;
        }
    }
    // 初始化channel
    private static void initChannel() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();

        // rpc 响应消息处理器，待实现
        RpcResponseMessageHandler RPC_HANDLER = new RpcResponseMessageHandler();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(group);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ProtocolFrameDecoder());
                ch.pipeline().addLast(LOGGING_HANDLER);
                ch.pipeline().addLast(MESSAGE_CODEC);
                ch.pipeline().addLast(RPC_HANDLER);
            }
        });
        try {
            channel = bootstrap.connect("localhost", 8080).sync().channel();
            channel.closeFuture().addListener(future -> {
                group.shutdownGracefully();
            });
        } catch (InterruptedException e) {
           log.error("client error",e);
        }
    }
}