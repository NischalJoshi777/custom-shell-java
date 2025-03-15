package com.shellSimulator;

import java.util.*;

public class CustomShell {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ShellCommand shellCommand = new ShellCommand();
        Scheduler scheduler = new Scheduler();

        
        while (true) {
            System.out.print("$ ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;

            String[] tokens = input.split(" ");
            String command = tokens[0];
            String[] arguments = Arrays.copyOfRange(tokens, 1, tokens.length);

            switch (command) {
                case "exit":
                    System.out.println("Exiting shell...");
                    scanner.close();
                    return;
                case "pwd":
                    System.out.println(System.getProperty("user.dir"));
                    break;
                case "cd":
                    shellCommand.changeDirectory(arguments);
                    break;
                case "echo":
                    System.out.println(String.join(" ", arguments));
                    break;
                case "clear":
                    System.out.print("\033[H\033[2J");
                    System.out.flush();
                    break;
                case "ls":
                    shellCommand.listFiles();
                    break;
                case "mkdir":
                    shellCommand.createDirectory(arguments);
                    break;
                case "rmdir":
                    shellCommand.removeDirectory(arguments);
                    break;
                case "rm":
                    shellCommand.removeFile(arguments);
                    break;
                case "touch":
                    shellCommand.touchFile(arguments);
                    break;
                case "jobs":
                    shellCommand.listJobs();
                    break;
                case "kill":
                    shellCommand.killProcess(arguments);
                    break;
                case "fg":
                    shellCommand.bringToForeground(arguments);
                    break;
                case "bg":
                    shellCommand.resumeInBackground(arguments);
                    break;
                case "roundrobin":
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
                    break;
                case "priorityscheduling":
                    System.out.print("Enter number of processes: ");
                    numProcesses = Integer.parseInt(scanner.nextLine());
                    processes = new ArrayList<>();
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
                    break;
                case "page_replacement_fifo":
                    shellCommand.pageReplacement(true);
                    break;
                case "page_replacement_lsu":
                    shellCommand.pageReplacement(false);
                    break;
                default:
                    shellCommand.executeCommand(tokens, false);
                    break;
            }
        }
    }
}
