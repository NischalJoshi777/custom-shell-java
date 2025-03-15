package com.shellSimulator.producer_consumer;

import com.shellSimulator.SchedulingProcess;

class Producer implements Runnable {
    private final SharedBuffer buffer;
    private final SchedulingProcess process;

    public Producer(SharedBuffer buffer, SchedulingProcess process) {
        this.buffer = buffer;
        this.process = process;
    }

    @Override
    public void run() {
        try {
            for (int i = 1; i <= 5; i++) {
                buffer.produce(i);
                Thread.sleep(process.burstTime); // Simulate production time
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

