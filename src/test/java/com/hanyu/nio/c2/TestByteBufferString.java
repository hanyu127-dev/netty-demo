package com.hanyu.nio.c2;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static com.hanyu.nio.c2.ByteBufferUtil.debugAll;

public class TestByteBufferString {
    public static void main(String[] args) {
        // 1. 字符串转为 ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put("hello".getBytes());
        debugAll(buffer);

        // 2. Charset
        ByteBuffer buffer1 = StandardCharsets.UTF_8.encode("hello");
        debugAll(buffer1);

        // 3. wrap
        ByteBuffer buffer2 = ByteBuffer.wrap("hello".getBytes());
        debugAll(buffer2);

        String string = StandardCharsets.UTF_8.decode(buffer).toString();
    }
}
