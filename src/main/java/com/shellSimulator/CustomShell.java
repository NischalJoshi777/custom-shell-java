package com.shellSimulator;

import java.util.*;

public class CustomShell {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ShellCommand shellCommand = new ShellCommand();
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
                shellCommand.changeDirectory(arguments);
            } else if (command.equals("echo")) {
                System.out.println(String.join(" ", arguments));
            } else if (command.equals("clear")) {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            } else if (command.equals("ls")) {
                shellCommand.listFiles();
            } else if (command.equals("mkdir")) {
                shellCommand.createDirectory(arguments);
            } else if (command.equals("rmdir")) {
                shellCommand.removeDirectory(arguments);
            } else if (command.equals("rm")) {
                shellCommand.removeFile(arguments);
            } else if (command.equals("touch")) {
                shellCommand.touchFile(arguments);
            } else if (command.equals("jobs")) {
                shellCommand.listJobs();
            } else if (command.equals("kill")) {
                shellCommand.killProcess(arguments);
            } else if (command.equals("fg")) {
                shellCommand.bringToForeground(arguments);
            } else if (command.equals("bg")) {
                shellCommand.resumeInBackground(arguments);
            } else {
                shellCommand.executeCommand(tokens, false); // Default: run in foreground
            }
        }
        scanner.close();
    }
}
