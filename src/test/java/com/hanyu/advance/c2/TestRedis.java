package com.hanyu.advance.c2;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

@Slf4j
public class TestRedis {
    /*
     set name zhangsan
     *3: 长度
     $3: set,三个字节
     set
     $4: name, 4个字节
     name
     $8
     zhangsan
     */

    final static byte[] LINE = {13,10};
    public static void main(String[] args) {

        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            ByteBuf buf= ctx.alloc().buffer();
                            buf.writeBytes("*3".getBytes());
                            buf.writeBytes(LINE);
                            buf.writeBytes("$3".getBytes());
                            buf.writeBytes(LINE);
                            buf.writeBytes("set".getBytes());
                            buf.writeBytes(LINE);
                            buf.writeBytes("$4".getBytes());
                            buf.writeBytes(LINE);
                            buf.writeBytes("name".getBytes());
                            buf.writeBytes(LINE);
                            buf.writeBytes("$8".getBytes());
                            buf.writeBytes(LINE);
                            buf.writeBytes("zhangsan".getBytes());
                            buf.writeBytes(LINE);

                            ctx.writeAndFlush(buf);
                        }
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                           ByteBuf buf = (ByteBuf) msg;
                           log.debug("{}",buf.toString(Charset.defaultCharset()));
                        }
                    });
                }
            });
            ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress("127.0.0.1", 6379));
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("client error",e);
        } finally {
            group.shutdownGracefully();
        }
    }
}
