package com.hanyu.netty.test;

import com.hanyu.config.Config;
import com.hanyu.message.LoginRequestMessage;
import com.hanyu.message.Message;
import com.hanyu.protocol.MessageCodecSharable;
import com.hanyu.protocol.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.logging.LoggingHandler;

public class TestSerializer {
    public static void main(String[] args) {
        MessageCodecSharable CODEC = new MessageCodecSharable();
        LoggingHandler LOGGIN = new LoggingHandler();
        EmbeddedChannel channel = new EmbeddedChannel(LOGGIN,CODEC,LOGGIN);


        LoginRequestMessage message = new LoginRequestMessage("zhangsan", "123");
        // 测试出栈
//        channel.writeOutbound(message);

        // 测试入栈
        ByteBuf buf = messageToBuf(message);
        channel.writeInbound(buf);

    }

    public static ByteBuf messageToBuf(Message message){
        int ordinal = Config.getSerializerAlgorithm().ordinal();
        ByteBuf out = ByteBufAllocator.DEFAULT.buffer();
        out.writeBytes(new byte[]{1,2,3,4});
        out.writeByte(1);
        out.writeByte(ordinal);
        out.writeByte(message.getMessageType());
        out.writeInt(message.getSequenceId());
        out.writeByte(0xff);
        byte[] bytes = Serializer.Algorithm.values()[ordinal].serialize(message);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
        return out;
    }
}
