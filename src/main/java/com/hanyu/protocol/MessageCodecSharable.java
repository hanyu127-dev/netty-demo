package com.hanyu.protocol;

import com.hanyu.config.Config;
import com.hanyu.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.List;

public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf, Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> outList) throws Exception {
        ByteBuf out = ctx.alloc().buffer();
        // 1. 4 字节的魔数
        out.writeBytes(new byte[]{1, 2, 3, 4});
        // 2. 1 字节的版本
        out.writeByte(1);
        // 3. 1 字节的序列化, 0 jdk  1 json
        out.writeByte(Config.getSerializerAlgorithm().ordinal());
        // 4. 1 字节的指令类型
        out.writeByte(msg.getMessageType());
        // 5. 4 字节的请求序列
        out.writeInt(msg.getSequenceId());
        // 对齐填充 2^n
        out.writeByte(0xff);

        // 前面总共11个字节
        // 6. 获取内容的字节数组,
        byte[] bytes = Config.getSerializerAlgorithm().serialize(msg);

        // 7, 正文长度
        out.writeInt(bytes.length);
        // 8. 消息正文
        out.writeBytes(bytes);

        System.out.println("1111111111111111111");
        outList.add(out);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 解码
        // 魔数
        int magicNum = in.readInt();
        // 版本
        byte version = in.readByte();
        // 序列化算法
        byte serializerAlgorithm = in.readByte();
        // 指令类型
        byte messageType = in.readByte();
        // 请求序列
        int sequenceId = in.readInt();
        in.readByte();

        // 正文长度
        int length = in.readInt();
        byte[] bytes = new byte[length];
        // 正文长队
        in.readBytes(bytes, 0, length);

        // 找到序列化算法
        Serializer.Algorithm algorithm = Serializer.Algorithm.values()[serializerAlgorithm];
        // 确定具体消息类型
        Class<?> messageClass = Message.getMessageClass(messageType);

        Object message = algorithm.deserialize(messageClass, bytes);

        out.add(message);
    }
}
