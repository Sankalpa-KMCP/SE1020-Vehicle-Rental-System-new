package com.sliit.vehiclerentalsystem01.user.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

/**
 * Reads pending master-admin requests from the same location the legacy JSP used.
 */
@Service
public class MasterAdminPendingService {

    public List<String> readPendingLines() {
        Path file = Paths.get(System.getProperty("user.home"), "RentalData", "pending_admins.txt");
        if (!Files.isRegularFile(file)) {
            return List.of();
        }
        try {
            return Files.readAllLines(file, StandardCharsets.UTF_8).stream()
                    .map(String::trim)
                    .filter(s -> !s.isEmpty() && !s.startsWith("#"))
                    .toList();
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }
}
