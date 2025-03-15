package com.shellSimulator;
import com.shellSimulator.philosopher.Philosopher;
import com.shellSimulator.producer_consumer.ProducerConsumberMain;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Semaphore;

public class ShellCommand {

    private static final Map<Integer, Process> jobs = new HashMap<>();
    private static int jobIdCounter = 1;


    private final FilePermissionManager filePermissionManager = new FilePermissionManager();

    public void listFiles() {
        File currentDir = new File(System.getProperty("user.dir"));
        File[] files = currentDir.listFiles();

        if (files != null) {
            for (File file : files) {
                System.out.println(file.getName());
            }
        }
    }

    public void removeFile(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: rm <filename>");
            return;
        }

        String filePath = System.getProperty("user.dir") + "/" + args[0];

        // Check permission before deleting
        if (!filePermissionManager.hasPermission(filePath, CustomShell.getCurrentRole(), "write")) {
            System.out.println("Permission denied: You cannot delete " + args[0]);
            return;
        }

        File file = new File(filePath);
        if (file.exists() && file.delete()) {
            System.out.println(args[0] + " deleted successfully.");
        } else {
            System.out.println("Failed to delete " + args[0]);
        }
    }

    public void touchFile(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: touch <filename>");
            return;
        }

        String filePath = System.getProperty("user.dir") + "/" + args[0];

        // Check permission before creating
        if (!filePermissionManager.hasPermission(filePath, CustomShell.getCurrentRole(), "write")) {
            System.out.println("Permission denied: You cannot create " + args[0]);
            return;
        }

        File file = new File(filePath);
        try {
            if (file.createNewFile()) {
                System.out.println(args[0] + " created successfully.");
            } else {
                System.out.println(args[0] + " already exists.");
            }
        } catch (IOException e) {
            System.out.println("Error creating file: " + e.getMessage());
        }
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
    public void diningPhilosopher() {
        int numOfSemaphore =5;

        Semaphore[] forks = new Semaphore[numOfSemaphore];
        Thread[] philosophers = new Thread[numOfSemaphore];

        for (int i = 0; i < numOfSemaphore ;i++) {
            forks[i] = new Semaphore(1);
        }

        for (int i = 0; i < numOfSemaphore; i++) {
            philosophers[i] = new Thread(new Philosopher(i, forks[i], forks[(i + 1) % numOfSemaphore]));
        }

        // Start all philosophers
        for (Thread philosopher : philosophers) {
            philosopher.start();
        }

        for (Thread philosopher : philosophers) {
            try {
                philosopher.join(); // Wait for the philosopher to finish
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

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

    public void producerConsumer() {
        ProducerConsumberMain.main(null);
   
    }
}
