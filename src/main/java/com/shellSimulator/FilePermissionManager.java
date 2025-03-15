package com.shellSimulator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class FilePermissionManager {
    private static final String PERMISSION_DB_FILE = "files.json";
    private JsonNode filePermissions;

    public FilePermissionManager() {
        loadPermissions();
    }

    private void loadPermissions() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            filePermissions = objectMapper.readTree(new File(PERMISSION_DB_FILE));
        } catch (IOException e) {
            System.err.println("Error loading file permissions: " + e.getMessage());
        }
    }

    public boolean hasPermission(String filePath, String role, String permissionType) {
        // Admin has full access to everything
        if (role.equals("admin")) {
            return true;
        }

        for (JsonNode file : filePermissions.path("files")) {
            String path = file.path("path").asText();

            if (path.equals(filePath) || path.equals("ANY")) { // Allow admin full access
                JsonNode rolePermissions = file.path("permissions").path(role);
                if (rolePermissions.isArray()) {
                    for (JsonNode perm : rolePermissions) {
                        if (perm.asText().equals(permissionType)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public Set<String> getUserPermissions(String filePath, String role) {
        Set<String> permissions = new HashSet<>();

        // Admin has all permissions
        if (role.equals("admin")) {
            permissions.add("read");
            permissions.add("write");
            permissions.add("execute");
            return permissions;
        }

        for (JsonNode file : filePermissions.path("files")) {
            if (file.path("path").asText().equals(filePath)) {
                JsonNode rolePermissions = file.path("permissions").path(role);
                if (rolePermissions.isArray()) {
                    for (JsonNode perm : rolePermissions) {
                        permissions.add(perm.asText());
                    }
                }
            }
        }
        return permissions;
    }
}
