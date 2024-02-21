package com.hanyu.nio.c4;

import com.hanyu.nio.c2.ByteBufferUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

@Slf4j
public class Server {
    public static void main(String[] args) throws IOException {
        // 1. 创建Selector , 管理多个channel
        Selector selector = Selector.open();

        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        // 2. 建立selector 与 channel 的联系，即注册
        // SelectionKey 将来事件发生后，通过它，可以知道事件以及是哪个channel发生的事件
        SelectionKey sscKey = ssc.register(selector, 0, null);
        // 指定管理员需要关注的事件 accept
        sscKey.interestOps(SelectionKey.OP_ACCEPT);
        log.debug("register key {}",sscKey); // @12bc6874

        ssc.bind(new InetSocketAddress(8080));
        while (true) {
           // 3. select 方法 , 也是阻塞方法 ,有事件，才继续向下执行
            selector.select();
            // 4. 处理事件， 获取所有可用的事件
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
            while (iter.hasNext()){
                SelectionKey key = iter.next();
                log.debug(" key {}",key);
                // 将key 移除， 但并没有真正的删除
                iter.remove();
                // 5. 区分事件类型
                if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel)key.channel();
                    SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);
                    ByteBuffer buffer = ByteBuffer.allocate(16);
                    // 附件
                    SelectionKey scKey = sc.register(selector, 0, buffer);
                    scKey.interestOps(SelectionKey.OP_READ);
                    log.debug("{}",sc);
                    log.debug("scKey: {}",scKey);
                } else if (key.isReadable()){
                    try {
                        SocketChannel channel = (SocketChannel) key.channel();  //@2d6a9952
                        // 获取附件
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        int read = channel.read(buffer);
                        if (read==-1){
                            key.cancel();
                        }else {
                            split(buffer);
                            if (buffer.position() == buffer.limit()){
                                ByteBuffer newByteBuffer = ByteBuffer.allocate(buffer.capacity() * 2);
                                buffer.flip();
                                newByteBuffer.put(buffer);
                                key.attach(newByteBuffer);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        key.cancel();  // 客户端出异常，需要将key取消，(从selector 的key集合中真正删除)
                    }
                }
            }
        }


    }

    private static void split(ByteBuffer source) {
        source.flip();
        for (int i = 0; i < source.limit(); i++) {
            if (source.get(i) == '\n'){
                int length = i+1-source.position();
                ByteBuffer target = ByteBuffer.allocate(length);
                for (int j = 0; j < length ; j++) {
                    target.put(source.get());
                }
                target.flip();
                ByteBufferUtil.debugAll(target);
            }
        }
        source.compact();
    }
}
