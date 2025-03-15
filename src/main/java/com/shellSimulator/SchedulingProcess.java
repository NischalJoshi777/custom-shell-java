package com.shellSimulator;

class SchedulingProcess {
    int id, priority, burstTime, arrivalTime, remainingTime, completionTime, startTime;

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
