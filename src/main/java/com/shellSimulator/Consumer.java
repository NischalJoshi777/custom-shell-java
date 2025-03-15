package com.shellSimulator;

class Consumer implements Runnable {
    private final SharedBuffer buffer;
    private final SchedulingProcess process;

    public Consumer(SharedBuffer buffer, SchedulingProcess process) {
        this.buffer = buffer;
        this.process = process;
    }

    @Override
    public void run() {
        try {
            for (int i = 1; i <= 5; i++) {
                buffer.consume();
                Thread.sleep(process.burstTime); // Simulate consumption time
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}