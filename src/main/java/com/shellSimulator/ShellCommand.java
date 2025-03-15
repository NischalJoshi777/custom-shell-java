package com.shellSimulator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.*;
import java.util.concurrent.Semaphore;

public class ShellCommand {

    private static final Map<Integer, Process> jobs = new HashMap<>();
    private static int jobIdCounter = 1;

    public void pageReplacement (boolean isFifo) {
        int capacity = 3; // Number of page frames
        int[] pages = {1, 2, 3, 4, 1, 2, 5, 1, 2, 3, 4, 5}; // Page reference string

        PageReplacement pr = new PageReplacement(capacity);

        if (isFifo) {
            pr.fifo(pages);
        } else {
            pr.lru(pages);
        }
    }

    public void roundRobin (Scanner scanner) {
        Scheduler scheduler = new Scheduler();

        System.out.print("Enter time slice (ms): ");
        int timeSlice = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter number of processes: ");
        int numProcesses = Integer.parseInt(scanner.nextLine());
        List<SchedulingProcess> processes = new ArrayList<>();
        for (int i = 1; i <= numProcesses; i++) {
            System.out.print("Enter arrival time for Process " + i + ": ");
            int arrivalTime = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter burst time for Process " + i + ": ");
            int burstTime = Integer.parseInt(scanner.nextLine());
            processes.add(new SchedulingProcess(i, 0, burstTime, arrivalTime)); // Priority is 0 for Round-Robin
        }

        scheduler.roundRobinScheduling(processes, timeSlice);
    }

    public void priorityscheduling(Scanner scanner) {
        Scheduler scheduler = new Scheduler();

        System.out.print("Enter number of processes: ");
        int numProcesses = Integer.parseInt(scanner.nextLine());
        List<SchedulingProcess> processes = new ArrayList<>();
        for (int i = 1; i <= numProcesses; i++) {
            System.out.print("Enter arrival time for Process " + i + ": ");
            int arrivalTime = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter burst time for Process " + i + ": ");
            int burstTime = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter priority for Process " + i + ": ");
            int priority = Integer.parseInt(scanner.nextLine());
            processes.add(new SchedulingProcess(i, priority, burstTime, arrivalTime));
        }

        scheduler.preemptivePriorityScheduling(processes);

    }

    public void changeDirectory(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: cd <directory>");
            return;
        }
        Path newPath = Paths.get(args[0]).toAbsolutePath();
        if (Files.isDirectory(newPath)) {
            System.setProperty("user.dir", newPath.toString());
        } else {
            System.out.println("Directory does not exist.");
        }
    }


    public void listFiles() {
        File dir = new File(System.getProperty("user.dir"));
        String[] files = dir.list();
        if (files != null) {
            for (String file : files) {
                System.out.println(file);
            }
        }
    }

    public void createDirectory(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: mkdir <directory>");
            return;
        }
        Path dirPath = Paths.get(args[0]);
        try {
            Files.createDirectories(dirPath);
        } catch (IOException e) {
            System.out.println("Error creating directory: " + e.getMessage());
        }
    }

    public void removeDirectory(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: rmdir <directory>");
            return;
        }
        Path dirPath = Paths.get(args[0]);
        try {
            Files.deleteIfExists(dirPath);
        } catch (IOException e) {
            System.out.println("Error removing directory: " + e.getMessage());
        }
    }

    public void removeFile(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: rm <filename>");
            return;
        }
        Path filePath = Paths.get(args[0]);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.out.println("Error removing file: " + e.getMessage());
        }
    }

    public void touchFile(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: touch <filename>");
            return;
        }
        Path filePath = Paths.get(args[0]);
        try {
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            } else {
                Files.setLastModifiedTime(filePath, FileTime.fromMillis(System.currentTimeMillis()));
            }
        } catch (IOException e) {
            System.out.println("Error touching file: " + e.getMessage());
        }
    }

    public void listJobs() {
        if (jobs.isEmpty()) {
            System.out.println("No background jobs.");
        } else {
            jobs.forEach((id, process) -> System.out.println("[" + id + "] " + process.pid()));
        }
    }

    public void killProcess(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: kill <pid>");
            return;
        }
        try {
            int pid = Integer.parseInt(args[0]);
            Process process = jobs.get(pid);
            if (process != null) {
                process.destroy();
                jobs.remove(pid);
                System.out.println("Process " + pid + " terminated.");
            } else {
                System.out.println("Process not found.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid PID format.");
        }
    }

    public void bringToForeground(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: fg <job_id>");
            return;
        }
        try {
            int jobId = Integer.parseInt(args[0]);
            Process process = jobs.get(jobId);
            if (process != null) {
                // Bring the job to the foreground and wait for it
                process.waitFor();
                jobs.remove(jobId);
                System.out.println("Job " + jobId + " brought to foreground.");
            } else {
                System.out.println("Job not found.");
            }
        } catch (NumberFormatException | InterruptedException e) {
            System.out.println("Error bringing job to foreground: " + e.getMessage());
        }
    }

    public void resumeInBackground(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: bg <job_id>");
            return;
        }
        try {
            int jobId = Integer.parseInt(args[0]);
            Process process = jobs.get(jobId);
            if (process != null) {
                // Resume the stopped job in the background
                new Thread(() -> {
                    try {
                        process.waitFor();
                        jobs.remove(jobId);
                        System.out.println("Job " + jobId + " finished.");
                    } catch (InterruptedException e) {
                        System.out.println("Job interrupted.");
                    }
                }).start();
                System.out.println("Job " + jobId + " resumed in background.");
            } else {
                System.out.println("Job not found.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid job ID format.");
        }
    }

    public void executeCommand(String[] command, boolean runInBackground) {
        // Check if the command ends with '&', indicating background execution
        if (command[command.length - 1].equals("&")) {
            runInBackground = true;
            command = Arrays.copyOfRange(command, 0, command.length - 1); // Remove '&'
        }

        try {
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.inheritIO();
            Process process = builder.start();

            if (runInBackground) {
                // Add the process to the job list
                jobs.put(jobIdCounter++, process);
                System.out.println("Job " + (jobIdCounter - 1) + " started in background.");
            } else {
                // Wait for the foreground process to complete
                process.waitFor();
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Error executing command: " + e.getMessage());
        }
    }

    public void producerConsumer() {
        SharedBuffer buffer = new SharedBuffer(5); // Buffer size = 5

        // Create producer and consumer processes
        SchedulingProcess producer1 = new SchedulingProcess(1, 1, 100, 0); // ID, priority, burstTime, arrivalTime
        SchedulingProcess consumer1 = new SchedulingProcess(2, 1, 150, 0);

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
