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

    public void preemptivePriorityScheduling(List<SchedulingProcess> processes) {
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
    
            // Simulate execution for 1ms (or a small time unit)
            int executionTime = 1; // Simulate 1ms at a time
            System.out.println("Executing Process " + process.id + " with priority " + process.priority + " for " + executionTime + "ms");
            try {
                Thread.sleep(executionTime); // Simulate execution
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    
            process.remainingTime -= executionTime;
            currentTime += executionTime;
    
            // Check if a higher-priority process has arrived
            for (SchedulingProcess p : processes) {
                if (p.arrivalTime <= currentTime && p.remainingTime > 0 && p.priority > process.priority) {
                    System.out.println("Preempting Process " + process.id + " for Process " + p.id);
                    priorityQueue.add(process); // Re-add the preempted process
                    priorityQueue.add(p); // Add the higher-priority process
                    break;
                }
            }
    
            // If the process hasn't completed, re-add it to the queue
            if (process.remainingTime > 0) {
                priorityQueue.add(process);
            } else {
                process.completionTime = currentTime;
                System.out.println("Process " + process.id + " completed.");
            }
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