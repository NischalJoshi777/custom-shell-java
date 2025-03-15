package com.shellSimulator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class UserAuth {
    private static final String USER_DB_FILE = "users.json";

    public static String authenticate(String username, String password) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(new File(USER_DB_FILE));
            JsonNode users = rootNode.path("users");

            for (JsonNode user : users) {
                if (user.path("username").asText().equals(username)) {
                    // Using plaintext comparison (insecure)
                    String storedPassword = user.path("password").asText();
                    if (storedPassword.equals(password)) {
                        return user.path("role").asText(); // Return role if authenticated
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading user database: " + e.getMessage());
        }
        return null; // Authentication failed
    }
}
