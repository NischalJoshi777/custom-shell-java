package com.shellSimulator.philosopher;

import java.util.concurrent.Semaphore;

public class Philosopher implements Runnable{

    private int id;
    private Semaphore leftFork;
    private Semaphore rightFork;

    public Philosopher(int id, Semaphore leftFork, Semaphore rightFork) {
        this.id = id;
        this.leftFork = leftFork;
        this.rightFork = rightFork;
    }

    private void think() {
        System.out.println("Philosopher " + id + " is thinking.");

        try {
            Thread.sleep((int) (Math.random() * 1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void eat() {
        System.out.println("Philosopher " + id + " is eating.");

        try {
            Thread.sleep((int) (Math.random() * 1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run() {
        int eatCount=0;
        while (eatCount < 3) {
            eatCount++;
            think();
            try{
                if (id % 2 == 0) {
                    leftFork.acquire();
                    rightFork.acquire();
                } else {
                    rightFork.acquire();
                    leftFork.acquire();
                }
                eat();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                leftFork.release();
                rightFork.release();
            }
        }
    }
}
