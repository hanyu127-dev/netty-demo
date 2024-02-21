package com.hanyu.client;

import com.hanyu.message.*;
import com.hanyu.protocol.MessageCodec;
import com.hanyu.protocol.ProtocolFrameDecoder;
import com.sun.corba.se.impl.protocol.giopmsgheaders.RequestMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class ChatClient {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        CountDownLatch WAIT_FOR_LOGIN = new CountDownLatch(1);
        AtomicBoolean LOGIN =new AtomicBoolean(false);
        try{
            ChannelFuture channelFuture = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ProtocolFrameDecoder());
//                            ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                            ch.pipeline().addLast(new MessageCodec());
                            ch.pipeline().addLast("client handler",new ChannelInboundHandlerAdapter(){
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    // 连接建立后触法active事件
                                    // 创建新的线程，接收用户在控制台的输入
                                    new Thread(()->{
                                        Scanner scanner = new Scanner(System.in);
                                        log.debug("请输入用户名");
                                        String username = scanner.nextLine();
                                        log.debug("请输入密码");
                                        String password = scanner.nextLine();
                                        LoginRequestMessage message = new LoginRequestMessage(username, password);
                                        ctx.writeAndFlush(message);
                                        log.debug("等待后续操作");
                                        try {
                                            WAIT_FOR_LOGIN.await();
                                        } catch (InterruptedException e) {
                                            throw new RuntimeException(e);
                                        }
                                        // 如果登陆失败
                                        if (!LOGIN.get()){
                                            ctx.channel().close();
                                            return;
                                        }

                                        while (true){
                                            System.out.println("==================================");
                                            System.out.println("send [username] [content]");
                                            System.out.println("gsend [group name] [content]");
                                            System.out.println("gcreate [group name] [m1,m2,m3...]");
                                            System.out.println("gmembers [group name]");
                                            System.out.println("gjoin [group name]");
                                            System.out.println("gquit [group name]");
                                            System.out.println("quit");
                                            System.out.println("==================================");
                                            String command = scanner.nextLine();
                                            String[] s = command.split(" ");
                                            switch (s[0]){
                                                case "send":
                                                    ctx.writeAndFlush(new ChatRequestMessage(username,s[1],s[2]));
                                                    break;
                                                case "gsend":
                                                    ctx.writeAndFlush(new GroupChatRequestMessage(username,s[1],s[2]));
                                                    break;
                                                case "gcreate":
                                                    Set<String> set = new HashSet<>(Arrays.asList(s[2].split(",")));
                                                    set.add(username);
                                                    ctx.writeAndFlush(new GroupCreateRequestMessage(s[1],set));
                                                    break;
                                                case "gmembers":
                                                    ctx.writeAndFlush(new GroupMembersRequestMessage(s[1]));
                                                    break;
                                                case "gjoin":
                                                    ctx.writeAndFlush(new GroupJoinRequestMessage(username,s[1]));
                                                    break;
                                                case "gquit":
                                                    ctx.writeAndFlush(new GroupQuitRequestMessage(username,s[1]));
                                                    break;
                                                case "quit":
                                                    ctx.channel().close();
                                                    break;
                                            }
                                        }
                                    },"system in").start();
                                }

                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    log.debug("msg{}",msg);
                                    if((msg instanceof LoginResponseMessage)){
                                        LoginResponseMessage response = (LoginResponseMessage) msg;
                                        if (response.isSuccess()) {
                                            // 如果登陆成功
                                            LOGIN.set(true);
                                        }
                                        // 唤醒线程
                                        WAIT_FOR_LOGIN.countDown();
                                    }

                                }
                            });
                        }
                    })
                    .connect(new InetSocketAddress("127.0.0.1", 8080));
            Channel channel = channelFuture.sync().channel();
            channel.closeFuture().sync();
        }catch (InterruptedException e){
            log.error("client error",e);
        }finally {
            group.shutdownGracefully();
        }
    }
}
