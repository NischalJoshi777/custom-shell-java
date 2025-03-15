package com.shellSimulator;
import java.util.concurrent.Semaphore;

class SharedBuffer {
    private final int[] buffer;
    private final Semaphore empty;
    private final Semaphore full;
    private final Semaphore mutex;
    private int count = 0;

    public SharedBuffer(int size) {
        buffer = new int[size];
        empty = new Semaphore(size); // Initially, all slots are empty
        full = new Semaphore(0);    // Initially, no slots are full
        mutex = new Semaphore(1);   // Mutex for buffer access
    }

    public void produce(int item) throws InterruptedException {
        empty.acquire(); // Wait for an empty slot
        mutex.acquire(); // Acquire the mutex
        buffer[count++] = item;
        System.out.println("Produced: " + item);
        mutex.release(); // Release the mutex
        full.release();  // Signal that a slot is full
    }

    public int consume() throws InterruptedException {
        full.acquire();  // Wait for a full slot
        mutex.acquire(); // Acquire the mutex
        int item = buffer[--count];
        System.out.println("Consumed: " + item);
        mutex.release(); // Release the mutex
        empty.release(); // Signal that a slot is empty
        return item;
    }
}
