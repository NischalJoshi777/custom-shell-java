package com.shellSimulator;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.util.*;

public class CustomShell {
    private static final Map<Integer, Process> jobs = new HashMap<>();
    private static int jobIdCounter = 1;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("$ ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;

            String[] tokens = input.split(" ");
            String command = tokens[0];
            String[] arguments = Arrays.copyOfRange(tokens, 1, tokens.length);

            if (command.equals("exit")) {
                System.out.println("Exiting shell...");
                break;
            } else if (command.equals("pwd")) {
                System.out.println(System.getProperty("user.dir"));
            } else if (command.equals("cd")) {
                changeDirectory(arguments);
            } else if (command.equals("echo")) {
                System.out.println(String.join(" ", arguments));
            } else if (command.equals("clear")) {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            } else if (command.equals("ls")) {
                listFiles();
            } else if (command.equals("mkdir")) {
                createDirectory(arguments);
            } else if (command.equals("rmdir")) {
                removeDirectory(arguments);
            } else if (command.equals("rm")) {
                removeFile(arguments);
            } else if (command.equals("touch")) {
                touchFile(arguments);
            } else if (command.equals("jobs")) {
                listJobs();
            } else if (command.equals("kill")) {
                killProcess(arguments);
            } else if (command.equals("fg")) {
                bringToForeground(arguments);
            } else if (command.equals("bg")) {
                resumeInBackground(arguments);
            } else {
                executeCommand(tokens, false); // Default: run in foreground
            }
        }
        scanner.close();
    }

    private static void changeDirectory(String[] args) {
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

    private static void listFiles() {
        File dir = new File(System.getProperty("user.dir"));
        String[] files = dir.list();
        if (files != null) {
            for (String file : files) {
                System.out.println(file);
            }
        }
    }

    private static void createDirectory(String[] args) {
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

    private static void removeDirectory(String[] args) {
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

    private static void removeFile(String[] args) {
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

    private static void touchFile(String[] args) {
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

    private static void listJobs() {
        if (jobs.isEmpty()) {
            System.out.println("No background jobs.");
        } else {
            jobs.forEach((id, process) -> System.out.println("[" + id + "] " + process.pid()));
        }
    }

    private static void killProcess(String[] args) {
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

    private static void bringToForeground(String[] args) {
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

    private static void resumeInBackground(String[] args) {
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

    private static void executeCommand(String[] command, boolean runInBackground) {
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
