package com.hanyu.nio.c2;

import java.nio.ByteBuffer;

import static com.hanyu.nio.c2.ByteBufferUtil.debugAll;

public class TestByteBufferRead {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{'a','b','c','d'});
        buffer.flip();
//        debugAll(buffer);
//        buffer.get(new byte[4]);
//        debugAll(buffer);
//        buffer.rewind();
//        debugAll(buffer);
//        System.out.println((char) buffer.get());
//        System.out.println((char) buffer.get());
//        buffer.mark();  // 加标记，索引为2 ，即是 c 的位置
//        System.out.println((char) buffer.get());
//        System.out.println((char) buffer.get());
//        debugAll(buffer);
//        buffer.reset(); // 将position 重置到索引2
//        debugAll(buffer);
//        System.out.println((char) buffer.get());
//        System.out.println((char) buffer.get());

        debugAll(buffer);
        System.out.println(buffer.get(3));
        debugAll(buffer);


    }
}
