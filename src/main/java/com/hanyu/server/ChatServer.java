package com.hanyu.server;

import com.hanyu.protocol.MessageCodec;
import com.hanyu.protocol.MessageCodecSharable;
import com.hanyu.protocol.ProtocolFrameDecoder;
import com.hanyu.server.handler.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatServer {
    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        LoginRequestMessageHandler LOGIN_HANDLER = new LoginRequestMessageHandler();
        ChatRequestMessageHandler CHAT_HANDLER = new ChatRequestMessageHandler();
        GroupChatRequestMessageHandler GROUP_CHAT_HANDLER = new GroupChatRequestMessageHandler();
        GroupCreateRequestMessageHandler GROUP_CREATE_HANDLER = new GroupCreateRequestMessageHandler();
        GroupJoinRequestMessageHandler GROUP_JOIN_HANDLER = new GroupJoinRequestMessageHandler();
        GroupQuitRequestMessageHandler GROUP_QUIT_HANDLER = new GroupQuitRequestMessageHandler();


        try {
            ChannelFuture future = new ServerBootstrap()
                    .group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ProtocolFrameDecoder());
                            ch.pipeline().addLast(LOGGING_HANDLER);
                            ch.pipeline().addLast(new MessageCodecSharable());
                            ch.pipeline().addLast(LOGIN_HANDLER);
                            ch.pipeline().addLast(CHAT_HANDLER);
                            ch.pipeline().addLast(GROUP_CHAT_HANDLER);
                            ch.pipeline().addLast(GROUP_CREATE_HANDLER);
                            ch.pipeline().addLast(GROUP_JOIN_HANDLER);
                            ch.pipeline().addLast(GROUP_QUIT_HANDLER);

                        }
                    })
                    .bind(8080);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("server error", e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

}
