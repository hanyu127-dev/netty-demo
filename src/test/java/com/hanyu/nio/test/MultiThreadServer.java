package com.hanyu.nio.test;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

import static com.hanyu.nio.c2.ByteBufferUtil.debugAll;
@Slf4j
public class MultiThreadServer {
    public static void main(String[] args) throws IOException {
        Thread.currentThread().setName("boss");
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        // selector
        Selector boss = Selector.open();
        SelectionKey bossKey = ssc.register(boss, 0, null);
        bossKey.interestOps(SelectionKey.OP_ACCEPT);

        ssc.bind(new InetSocketAddress(8080));
        Worker[] workers = new Worker[2];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Worker("worker-"+i);
        }
        AtomicInteger index = new AtomicInteger();
        while (true){
            boss.select();
            Iterator<SelectionKey> iterator = boss.selectedKeys().iterator();
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()){
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);

                    log.debug("connected...{}",sc.getRemoteAddress());
                    // 关联 selector
                    log.debug("before register...{}",sc.getRemoteAddress());
                    workers[index.getAndIncrement() %workers.length].register(sc);

                    log.debug("after register...{}",sc.getRemoteAddress());

                }
            }
        }
    }

    static class Worker implements Runnable{
        private Thread thread;
        private Selector selector;
        private String name;
        private volatile boolean start = false;
        private ConcurrentLinkedDeque<Runnable> queue = new ConcurrentLinkedDeque<>();

        public Worker(String name) {
            this.name = name;
        }
        // 初始化线程和selector
        public void register(SocketChannel sc) throws IOException {
            if (!start){
                selector = Selector.open();
                thread = new Thread(this,name);
                thread.start();
                start = true;
            }
            queue.add(()->{
                try {
                    sc.register(selector,SelectionKey.OP_READ,null);
                } catch (ClosedChannelException e) {
                    throw new RuntimeException(e);
                }
            });
            // 唤醒 selector, 即是 select() 方法向下执行
            selector.wakeup();

        }

        @Override
        public void run() {
            while (true){
                try{

                    selector.select();
                    Runnable task = queue.poll();
                    if (task!=null){
                        task.run();
                    }
                    Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                    while (iter.hasNext()){
                        SelectionKey key = iter.next();
                        iter.remove();
                        if (key.isReadable()){
                            try {
                                ByteBuffer buffer = ByteBuffer.allocate(16);
                                SocketChannel channel = (SocketChannel) key.channel();
                                log.debug("read...{}",channel.getRemoteAddress());
                                channel.read(buffer);
                                buffer.flip();
                                debugAll(buffer);
                            } catch (IOException e) {
                                e.printStackTrace();
                                key.cancel();
                            }
                        }
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
