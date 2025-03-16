package com.shellSimulator;

import java.io.*;
import java.util.*;

public class PipingShell {

    public static void executePipedCommands(String commandLine) throws IOException {
        String[] commands = commandLine.split("\\|"); // Split commands by "|"
        Process previousProcess = null;
        InputStream previousOutput = null;

        for (int i = 0; i < commands.length; i++) {
            String[] command = commands[i].trim().split("\\s+");
            ProcessBuilder pb = new ProcessBuilder(command);

            Process process = pb.start();

            if (previousOutput != null) {
                final InputStream pipeInput = previousOutput;  // Capture as final
                final OutputStream processInput = process.getOutputStream(); // Capture as final

                // Create a new thread for transferring data between processes
                Thread pipeThread = new Thread(() -> {
                    try {
                        transferStream(pipeInput, processInput);
                    } catch (IOException e) {
                        System.err.println("Pipe error: " + e.getMessage());
                    }
                });
                pipeThread.start();
            }

            previousProcess = process;
            previousOutput = process.getInputStream();
        }

        // Print the final output
        if (previousProcess != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(previousOutput))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
        }
    }

    public static void transferStream(InputStream input, OutputStream output) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output))) {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
                writer.flush();
            }
        }
    }
}
