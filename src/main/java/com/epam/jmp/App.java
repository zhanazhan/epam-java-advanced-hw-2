package com.epam.jmp;

import com.epam.jmp.multithreading.*;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args ) throws InterruptedException {
        System.out.println( "Hello World!" );
        // Task 1
        DasExperiment.main(args);
        // Task 2
        DeadlockExample.main(args);
        // Task 3
        MessageBus.main(args);
        // Task 4
        BlockingObjectPool.main(args);
        // Task 5
        CurrencyExchangeApp.main(args);
        // Task 6
        ProducerConsumerClassic.main(args);
    }
}
