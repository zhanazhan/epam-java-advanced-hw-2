package com.epam.jmp.multithreading;

import java.util.*;

public class DeadlockExample {
    public static void main(String[] args) {
        List<Integer> numbers = new ArrayList<>();
        Object lock = new Object();

        Thread writer = new Thread(() -> {
            Random random = new Random();
            while (true) {
                synchronized (lock) {
                    numbers.add(random.nextInt(100));
                }
                try { Thread.sleep(100); } catch (InterruptedException ignored) {}
            }
        });

        Thread sumCalculator = new Thread(() -> {
            while (true) {
                synchronized (lock) {
                    int sum = numbers.stream().mapToInt(Integer::intValue).sum();
                    System.out.println("Sum: " + sum);
                }
                try { Thread.sleep(100); } catch (InterruptedException ignored) {}
            }
        });

        Thread sqrtCalculator = new Thread(() -> {
            while (true) {
                synchronized (lock) {
                    int sumOfSquares = numbers.stream().mapToInt(n -> n * n).sum();
                    System.out.println("Sqrt of sum of squares: " + Math.sqrt(sumOfSquares));
                }
                try { Thread.sleep(100); } catch (InterruptedException ignored) {}
            }
        });

        writer.start();
        sumCalculator.start();
        sqrtCalculator.start();
    }
}

