package com.shellSimulator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
}
