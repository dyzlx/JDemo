package com.dyz.demo.basic.comsumerproducers.waitnotify;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainTest {
    public static void main(String[] args) throws InterruptedException {
        //任务队列
        Queue<String> taskQueue = new LinkedList<>();
        //线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(15);
        //5个生产者
        for(int i = 0 ; i < 5 ; i++) {
            threadPool.submit(new Producer(taskQueue, 10));
        }
        //10个消费者
        for(int i = 0 ; i < 10 ; i++) {
            threadPool.submit(new Consumer(taskQueue));
        }
        //运行一会后立即关闭JVM
        Thread.sleep(2);
        System.exit(0);
    }
}
