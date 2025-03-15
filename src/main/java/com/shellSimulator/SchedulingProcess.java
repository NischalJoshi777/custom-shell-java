package com.shellSimulator;

public class SchedulingProcess {
    ///TODO: create getter setters instead of making it public for encapsulation

    public int id, priority;
    public int burstTime;
    public int arrivalTime;
    public int remainingTime;
    public int completionTime;
    public int startTime;

    public SchedulingProcess(int id, int priority, int burstTime, int arrivalTime) {
        this.id = id;
        this.priority = priority;
        this.burstTime = burstTime;
        this.arrivalTime = arrivalTime;
        this.remainingTime = burstTime;
        this.completionTime = 0;
        this.startTime = -1;
    }
}
