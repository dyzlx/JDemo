package com.dyz.demo.basic.comsumerproducers.waitnotify;

import java.util.Queue;

class Consumer implements Runnable {
    /**
     * 任务队列
     */
    private Queue<String> queue;

    public Consumer(Queue<String> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        //消费者不断的消费
        while (true) {
            synchronized (queue) {
                try {
                    //如果任务队列是空的就等待
                    /**
                     * 这里使用while判断而不是使用if的原因：
                     * 当某处调用notify时，该代码会从wait方法上返回
                     * 但是此时有可能其他消费者线程在该线程从wait返回之前已经将队列中消费空
                     * 所以此时queue.isEmpty又空了
                     * 所以应该使用while
                     * 在该线程退出wait方法后返回再继续判断queue.isEmpty条件是否成立
                     */
                    while (queue.isEmpty()) {
                        System.out.println("Task Queue is empty, Consumer:" + Thread.currentThread().getName() + " wait !");
                        queue.wait();
                        System.out.println("Consumer:" + Thread.currentThread().getName() + "exit wait !");
                    }
                    //消费者消费任务，移除任务队列
                    String task = queue.remove();
                    System.out.println("Consumer:" + Thread.currentThread().getName() + " consum " + task + "!");
                    //通知在任务队列上等待的其他线程
                    queue.notifyAll();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}