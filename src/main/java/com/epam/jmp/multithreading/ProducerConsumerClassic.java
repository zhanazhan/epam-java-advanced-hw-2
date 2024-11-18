package com.epam.jmp.multithreading;

import java.util.*;

public class ProducerConsumerClassic {
    private static final Queue<Integer> queue = new LinkedList<>();
    private static final int MAX_SIZE = 10;

    public synchronized void produce(Integer item) throws InterruptedException {
        while (queue.size() == MAX_SIZE) {
            wait();
        }
        queue.offer(item);
        notifyAll();
    }

    public synchronized Integer consume() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        Integer item = queue.poll();
        notifyAll();
        return item;
    }

    public static void main(String[] args) {
        ProducerConsumerClassic producerConsumerClassic = new ProducerConsumerClassic();
        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < 50; i++) {
                    producerConsumerClassic.produce(i);
                    System.out.println("Produced: " + i);
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 50; i++) {
                    Integer item = producerConsumerClassic.consume();
                    System.out.println("Consumed: " + item);
                    Thread.sleep(200);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();
    }
}
