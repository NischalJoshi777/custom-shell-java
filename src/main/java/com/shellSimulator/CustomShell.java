package com.shellSimulator;
import java.util.*;

public class CustomShell {
    //creating user and role
    private static String currentUser;
    private static String currentRole;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Secure Shell!");
        while (true) {
            System.out.print("Username: ");
            String username = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();
            String role = UserAuth.authenticate(username, password);
            if (role != null) {
                currentUser = username;
                currentRole = role;
                System.out.println("Login successful! Role: " + role);
                break;
            } else {
                System.out.println("Invalid credentials. Try again.");
            }
        }

        ShellCommand shellCommand = new ShellCommand();
        while (true) {
            System.out.print(currentUser + "@shell$ ");
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
                    System.out.println("Cleared shell...");
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
                    shellCommand.roundRobin(scanner);
                    break;
                case "priorityscheduling":
                    shellCommand.priorityscheduling(scanner);
                    break;
                case "page_replacement_fifo":
                    shellCommand.pageReplacement(true);
                    break;
                case "page_replacement_lsu":
                    shellCommand.pageReplacement(false);
                    break;
                case "producer_consumer":
                    shellCommand.producerConsumer();
                    break;
                case "dining_philosopher":
                    shellCommand.diningPhilosopher();
                    break;
                default:
                    shellCommand.executeCommand(tokens, false);
                    break;
            }
    }}
    public static String getCurrentRole() {
        return currentRole;
    }

}

