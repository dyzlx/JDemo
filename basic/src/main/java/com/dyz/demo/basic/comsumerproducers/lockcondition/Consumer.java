package com.dyz.demo.basic.comsumerproducers.lockcondition;

import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

class Consumer implements Runnable {

    private Queue<String> queue;

    private Lock lock;

    private Condition fullCondition;

    private Condition emptyCondition;

    public Consumer(Queue<String> queue, Lock lock, Condition fullCondition, Condition emptyCondition) {
        this.queue = queue;
        this.lock = lock;
        this.fullCondition = fullCondition;
        this.emptyCondition = emptyCondition;
    }

    @Override
    public void run() {
        while(true) {
            lock.lock();
            try {
                while(queue.isEmpty()) {
                    System.out.println("Task Queue is empty, ASConsumer:" + Thread.currentThread().getName() + " wait !");
                    emptyCondition.await();
                    System.out.println("ASConsumer:" + Thread.currentThread().getName() + "exit wait !");
                }
                String task = queue.remove();
                System.out.println("ASConsumer:" + Thread.currentThread().getName() + " consum " + task + "!");
                fullCondition.signalAll();
            }
            catch(InterruptedException e) {
                e.printStackTrace();
            }
            finally {
                lock.unlock();
            }
        }
    }
}
