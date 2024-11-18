package com.epam.jmp.multithreading;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.*;
import java.util.concurrent.*;

public class HashMapPerformance {

    public static void main(String[] args) throws InterruptedException {
        int numThreads = 4; // Number of threads
        int numElements = 1000000; // Number of elements to add

        // HashMap and ConcurrentHashMap
        HashMap<Integer, Integer> hashMap = new HashMap<>();
        ConcurrentHashMap<Integer, Integer> concurrentHashMap = new ConcurrentHashMap<>();

        // Measure HashMap performance
        System.out.println("Testing with HashMap");
        long hashMapTime = testMapPerformance(hashMap, numThreads, numElements);
        System.out.println("HashMap Time: " + hashMapTime + " ms");

        // Measure ConcurrentHashMap performance
        System.out.println("Testing with ConcurrentHashMap");
        long concurrentHashMapTime = testMapPerformance(concurrentHashMap, numThreads, numElements);
        System.out.println("ConcurrentHashMap Time: " + concurrentHashMapTime + " ms");

        printRuntime();
    }

    // Test method for performance measurement
    public static long testMapPerformance(Map<Integer, Integer> map, int numThreads, int numElements) throws InterruptedException {
        List<Thread> threads = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        // Start threads to populate the map
        for (int i = 0; i < numThreads; i++) {
            threads.add(new Thread(() -> {
                for (int j = 0; j < numElements / numThreads; j++) {
                    map.put(j, j);
                }
            }));
        }

        // Start threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Join threads
        for (Thread thread : threads) {
            thread.join();
        }

        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }

    public static void printRuntime() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

        System.out.println("JVM Name: " + runtimeMXBean.getName());
        System.out.println("JVM Vendor: " + runtimeMXBean.getVmVendor());
        System.out.println("JVM Version: " + runtimeMXBean.getVmVersion());
        System.out.println("JVM Start Time (ms): " + runtimeMXBean.getStartTime());
        System.out.println("JVM Uptime (ms): " + runtimeMXBean.getUptime());
        System.out.println("Java Version: " + System.getProperty("java.version"));
        System.out.println("Java Home: " + System.getProperty("java.home"));
    }
}

