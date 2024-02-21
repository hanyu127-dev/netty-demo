package com.hanyu.protocol;

import com.hanyu.message.LoginRequestMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class TestMessageCodec {
    public static void main(String[] args) throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(1024,12,4,0,0),
                new LoggingHandler(LogLevel.DEBUG),
                new MessageCodec()
        );

        LoginRequestMessage message = new LoginRequestMessage("zhangsan", "123");
        channel.writeOutbound(message);
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();

        // 切片证明粘包半包
        new MessageCodec().encode(null,message,buf);
        ByteBuf s1 = buf.slice(0, 100);
        s1.retain();
        ByteBuf s2 = buf.slice(100, buf.readableBytes() - 100);
        // 入栈
        channel.writeInbound(s1);  // 会调用release
        channel.writeInbound(s2);
    }
}
