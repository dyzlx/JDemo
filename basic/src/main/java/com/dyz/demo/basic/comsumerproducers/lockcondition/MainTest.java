package com.dyz.demo.basic.comsumerproducers.lockcondition;

import java.lang.management.ManagementFactory;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MainTest {

    private static Lock lock = new ReentrantLock();

    //任务队列满的条件
    private static Condition fullCondition = lock.newCondition();

    //任务队列空的条件
    private static Condition emptyCondition = lock.newCondition();

    public static void main(String[] args) throws InterruptedException {
        //任务队列
        Queue<String> taskQueue = new LinkedList<>();
        //线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(15);
        //5个生产者
        for(int i = 0 ; i < 5 ; i++) {
            threadPool.submit(new Producer(taskQueue, 10, lock, fullCondition, emptyCondition));
        }
        //10个消费者
        for(int i = 0 ; i < 10 ; i++) {
            threadPool.submit(new Consumer(taskQueue, lock, fullCondition, emptyCondition));
        }
        //运行一会后立即关闭JVM
        Thread.sleep(2);
        System.exit(0);
    }
}
