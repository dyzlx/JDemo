package com.dyz.demo.basic.comsumerproducers.lockcondition;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

class Producer implements Runnable {

    private Queue<String> queue;

    private int maxLength;

    private Lock lock;

    private Condition fullCondition;

    private Condition emptyCondition;

    public Producer(Queue<String> queue, int max, Lock lock, Condition fullCondition, Condition emptyCondition) {
        this.queue = queue;
        this.lock = lock;
        this.maxLength = max;
        this.fullCondition = fullCondition;
        this.emptyCondition = emptyCondition;
    }

    @Override
    public void run() {
        while(true) {
            lock.lock();
            try {
                while(queue.size() >= this.maxLength) {
                    System.out.println("Task Queue is full, ASProducer:" + Thread.currentThread().getName() + " wait !");
                    fullCondition.await();
                    System.out.println("ASProducer:" + Thread.currentThread().getName() + "exit wait !");
                }
                String task = "Task-"+new Random().nextInt();
                queue.add(task);
                System.out.println("ASProducer:" + Thread.currentThread().getName() + " product " + task + "!");
                emptyCondition.signalAll();
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