package com.dyz.demo.basic.comsumerproducers.waitnotify;

import java.util.Queue;
import java.util.Random;

class Producer implements Runnable {
    /**
     * 任务队列
     */
    private Queue<String> queue;
    /**
     * 队列的最大长度
     */
    private int queueMaxLength;

    public Producer(Queue<String> queue, int lenght) {
        this.queue = queue;
        this.queueMaxLength = lenght;
    }

    @Override
    public void run() {
        //生产者不断的执行生产过程
        while(true) {
            synchronized(queue) {
                try {
                    //队列满了就等待
                    /**
                     * 这里使用while判断而不是使用if的原因：
                     * 当某处调用notify时，该代码会从wait方法上返回
                     * 但是此时有可能其他生产者线程在该线程从wait返回之前已经将队列中填满
                     * 所以此时队列又满了
                     * 所以应该使用while
                     * 在该线程退出wait方法后返回再继续判断队列是否已满条件是否成立
                     */
                    while(queue.size() >= queueMaxLength) {
                        System.out.println("Task Queue is full, Producer:" + Thread.currentThread().getName() + " wait !");
                        queue.wait();
                        System.out.println("Producer:" + Thread.currentThread().getName() + "exit wait !");
                    }
                    //生产者生产任务，加入任务队列
                    String task = "Task-"+new Random().nextInt();
                    queue.add(task);
                    System.out.println("Producer:" + Thread.currentThread().getName() + " product " + task + "!");
                    //通知在queue等待的其他的生产者和消费者线程
                    queue.notifyAll();
                }
                catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}