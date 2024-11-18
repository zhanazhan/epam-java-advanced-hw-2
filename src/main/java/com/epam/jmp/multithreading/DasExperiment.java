package com.epam.jmp.multithreading;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DasExperiment {
    public static void main(String[] args) throws InterruptedException {
        // Initial Map
        Map<Integer, Integer> map = new HashMap<>();

        // Unsafe Example
        Thread writer = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                map.put(i, i);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ignored) {
                }
            }
        });

        Thread reader = new Thread(() -> {
            int sum = 0;
            try {
                for (var value : map.values()) {
                    sum += value;
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException ignored) {
                    }
                }
            } catch (ConcurrentModificationException e) {
                System.out.println("Caught ConcurrentModificationException!");
            }
        });

        writer.start();
        reader.start();

        writer.join();
        reader.join();

        System.out.println("Map size (HashMap): " + map.size());

        // Using ConcurrentHashMap
        Map<Integer, Integer> concurrentMap = new ConcurrentHashMap<>();
        testSafeMap(concurrentMap);

        // Using Collections.synchronizedMap
        Map<Integer, Integer> synchronizedMap = Collections.synchronizedMap(new HashMap<>());
        testSafeMap(synchronizedMap);
    }

    private static void testSafeMap(Map<Integer, Integer> map) throws InterruptedException {
        Thread writer = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                map.put(i, i);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ignored) {
                }
            }
        });

        Thread reader = new Thread(() -> {
            synchronized (map) { // Explicit synchronization for Collections.synchronizedMap
                int sum = 0;
                for (var value : map.values()) {
                    sum += value;
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException ignored) {
                    }
                }
                System.out.println("Sum of values: " + sum);
            }
        });

        writer.start();
        reader.start();

        writer.join();
        reader.join();
        System.out.println("Map size: " + map.size());
    }
}

