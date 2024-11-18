package com.epam.jmp.multithreading;

import java.util.ArrayList;
import java.util.List;

public class MessageBus {
    private final List<String> queue = new ArrayList<>();
    private final Object lock = new Object();

    public void produce(String message) {
        synchronized (lock) {
            queue.add(message);
            lock.notifyAll();
        }
    }

    public String consume() {
        synchronized (lock) {
            while (queue.isEmpty()) {
                try {
                    lock.wait();
                } catch (InterruptedException ignored) {}
            }
            return queue.remove(0);
        }
    }

    public static void main(String[] args) {
        MessageBus bus = new MessageBus();

        Thread producer = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                bus.produce("Message " + i);
                System.out.println("Produced: Message " + i);
                try { Thread.sleep(100); } catch (InterruptedException ignored) {}
            }
        });

        Thread consumer = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                String message = bus.consume();
                System.out.println("Consumed: " + message);
            }
        });

        producer.start();
        consumer.start();
    }
}
