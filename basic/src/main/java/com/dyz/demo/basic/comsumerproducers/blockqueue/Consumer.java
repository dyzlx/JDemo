package com.dyz.demo.basic.comsumerproducers.blockqueue;

import java.util.concurrent.BlockingQueue;

class Consumer implements Runnable {

    private BlockingQueue<String> queue;

    public Consumer(BlockingQueue<String> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while(true) {
            try {
                String task = queue.take();
                System.out.println("BQConsumer:" + Thread.currentThread().getName() + " consume " + task + "!");
            }
            catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}