package com.dyz.demo.basic.comsumerproducers.blockqueue;

import java.lang.management.ManagementFactory;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class MainTest {

    public static void main(String[] args) throws InterruptedException {
        //使用阻塞队列
        BlockingQueue<String> blockQueue = new LinkedBlockingQueue<>();
        //线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(15);
        //5个生产者
        for(int i = 0 ; i < 5 ; i++) {
            threadPool.submit(new Producer(blockQueue));
        }
        //10个消费者
        for(int i = 0 ; i < 10 ; i++) {
            threadPool.submit(new Consumer(blockQueue));
        }
        //运行一会后立即关闭JVM
        Thread.sleep(2);
        System.exit(0);
    }
}
