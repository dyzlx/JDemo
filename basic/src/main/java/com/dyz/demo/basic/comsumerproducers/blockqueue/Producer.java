package com.dyz.demo.basic.comsumerproducers.blockqueue;

import java.util.Random;
import java.util.concurrent.BlockingQueue;

class Producer implements Runnable {

    private BlockingQueue<String> queue;

    public Producer(BlockingQueue<String> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while(true) {
            String task = "Task-"+new Random().nextInt();
            try {
                queue.put(task);
                System.out.println("BQProducer:" + Thread.currentThread().getName() + " product " + task + "!");
            }
            catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}