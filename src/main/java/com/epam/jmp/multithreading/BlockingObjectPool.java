package com.epam.jmp.multithreading;

import java.util.LinkedList;
import java.util.Queue;

public class BlockingObjectPool {
    private final Queue<Object> pool;
    private final int size;

    public BlockingObjectPool(int size) {
        this.size = size;
        this.pool = new LinkedList<>();
    }

    public synchronized Object get() throws InterruptedException {
        while (pool.isEmpty()) {
            wait();
        }
        Object obj = pool.poll();
        notifyAll();
        return obj;
    }

    public synchronized void take(Object obj) throws InterruptedException {
        while (pool.size() == size) {
            wait();
        }
        pool.offer(obj);
        notifyAll();
    }

    public static void main(String[] args) {
        BlockingObjectPool pool = new BlockingObjectPool(2);

        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    pool.take("Object " + i);
                    System.out.println("Added: Object " + i);
                }
            } catch (InterruptedException ignored) {}
        });

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    Object obj = pool.get();
                    System.out.println("Consumed: " + obj);
                }
            } catch (InterruptedException ignored) {}
        });

        producer.start();
        consumer.start();
    }
}
