package com.shellSimulator;
import java.util.*;


class Scheduler {
    public void roundRobinScheduling(List<SchedulingProcess> processes, int timeSlice) {
        Queue<SchedulingProcess> queue = new LinkedList<>();
        int currentTime = 0;

        // Add processes to the queue
        queue.addAll(processes);

        while (!queue.isEmpty()) {
            SchedulingProcess process = queue.poll();

            // Set start time if not already set
            if (process.startTime == -1) {
                process.startTime = currentTime;
            }

            // Simulate execution for the time slice or remaining time
            int executionTime = Math.min(process.remainingTime, timeSlice);
            System.out.println("Executing Process " + process.id + " for " + executionTime + "ms");
            try {
                Thread.sleep(executionTime); // Simulate execution
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            process.remainingTime -= executionTime;
            currentTime += executionTime;

            // Re-add to queue if the process hasn't completed
            if (process.remainingTime > 0) {
                queue.add(process);
            } else {
                process.completionTime = currentTime;
                System.out.println("Process " + process.id + " completed.");
            }
        }

        // Calculate and display performance metrics
        calculateMetrics(processes);
    }

    public void priorityScheduling(List<SchedulingProcess> processes) {
        PriorityQueue<SchedulingProcess> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(p -> -p.priority));
        int currentTime = 0;

        // Add processes to the priority queue
        priorityQueue.addAll(processes);

        while (!priorityQueue.isEmpty()) {
            SchedulingProcess process = priorityQueue.poll();

            // Set start time if not already set
            if (process.startTime == -1) {
                process.startTime = currentTime;
            }

            // Simulate execution for the entire burst time
            System.out.println("Executing Process " + process.id + " with priority " + process.priority);
            try {
                Thread.sleep(process.burstTime); // Simulate execution
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            currentTime += process.burstTime;
            process.completionTime = currentTime;
            System.out.println("Process " + process.id + " completed.");
        }

        // Calculate and display performance metrics
        calculateMetrics(processes);
    }

    private void calculateMetrics(List<SchedulingProcess> processes) {
        System.out.println("\nPerformance Metrics:");
        for (SchedulingProcess process : processes) {
            int turnaroundTime = process.completionTime - process.arrivalTime;
            int waitingTime = turnaroundTime - process.burstTime;
            int responseTime = process.startTime - process.arrivalTime;

            System.out.println("Process " + process.id + ":");
            System.out.println("  Turnaround Time: " + turnaroundTime + "ms");
            System.out.println("  Waiting Time: " + waitingTime + "ms");
            System.out.println("  Response Time: " + responseTime + "ms");
        }
    }
}