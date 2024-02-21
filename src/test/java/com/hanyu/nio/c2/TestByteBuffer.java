package com.hanyu.nio.c2;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
@Slf4j
public class TestByteBuffer {
    public static void main(String[] args) {
        // FileChannel
        // 1.输入输出流 2.RandomAccessFile
        try (FileChannel channel = new FileInputStream("data.txt").getChannel()) {
            // 准备缓存区 10字节
            ByteBuffer buffer = ByteBuffer.allocate(10);
            // 从 channel 读，写到 buffer
            while (true){
                int len = channel.read(buffer);
                log.debug("读取到的字节数 {}",len);
                if (len == -1){
                    break;
                }
                // 打印 buffer 的内容
                buffer.flip(); // 切换至读模式
                while (buffer.hasRemaining()){  // 是否还有剩余的数据
                    byte b = buffer.get();
                    log.debug("实际字节 {}",(char) b);
                }
                // 切换至写模式
                buffer.clear();
            }
        } catch (IOException e) {
        }

    }
}
