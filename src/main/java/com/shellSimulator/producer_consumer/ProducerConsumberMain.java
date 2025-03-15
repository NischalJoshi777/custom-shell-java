package com.shellSimulator.producer_consumer;

import com.shellSimulator.SchedulingProcess;

public class ProducerConsumberMain {
    public static void main(String[] args) {
        SharedBuffer buffer = new SharedBuffer(5); // Buffer size = 5
        // Create producer and consumer processes
        SchedulingProcess producer1 = new SchedulingProcess(1, 1, 200, 0); // slower production
        SchedulingProcess consumer1 = new SchedulingProcess(2, 1, 100, 0); // faster consumption

        // Create and start producer and consumer threads
        Thread producerThread = new Thread(new Producer(buffer, producer1));
        Thread consumerThread = new Thread(new Consumer(buffer, consumer1));

        producerThread.start();
        consumerThread.start();

        try {
            producerThread.join();
            consumerThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
