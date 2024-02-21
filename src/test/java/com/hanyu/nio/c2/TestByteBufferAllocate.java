package com.hanyu.nio.c2;

import java.nio.ByteBuffer;

public class TestByteBufferAllocate {
    public static void main(String[] args) {
        System.out.println(ByteBuffer.allocate(16).getClass());
        System.out.println(ByteBuffer.allocateDirect(16).getClass());
        /*
            class java.nio.HeapByteBuffer  -- java的堆内存，读写效率较低，会受到垃圾回收的影响（垃圾回收会导致数据的来回搬迁）
            class java.nio.DirectByteBuffer -- 直接内存， 读写效率高（少一次拷贝），不会受到GC的影响，分配内存的效率低，可能会造成内存泄露
         */


    }
}
