package com.hanyu.netty.test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

import static com.hanyu.netty.c4.TestByteBuf.log;

@Slf4j
public class EchoServer {
    public static void main(String[] args) {
        ChannelFuture future = new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf = (ByteBuf) msg;
                                EchoServer.log.debug("客户端发来消息： {}", buf.toString(Charset.defaultCharset()));
                                log(buf);

//                                // 准备会写消息
//                                ByteBuf response = ctx.alloc().buffer();
//                                response.writeBytes(buf);
//                                log(response);
//                                log.debug("==========");
//                                log(buf);
//                                buf.release();
                                ctx.writeAndFlush(buf);
                                buf.release();

                            }
                        });
                    }
                })
                .bind(8080);
    }
}
