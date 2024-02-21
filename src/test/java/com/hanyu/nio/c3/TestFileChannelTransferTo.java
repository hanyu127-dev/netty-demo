package com.hanyu.nio.c3;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class TestFileChannelTransferTo {
    public static void main(String[] args) {
        try(
                FileChannel from = new FileInputStream("data.txt").getChannel();  // 只能读
                FileChannel to = new FileOutputStream("to.txt").getChannel(); // 只能写
        ){
            // 效率高， 底层会利用操作系统的零拷贝进行优化
            long size = from.size();
            // left 表示还剩多少字节
            for (long  left = size; left >0 ;) {
                left -= from.transferTo((size-left),left,to);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
