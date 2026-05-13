package com.sliit.vehiclerentalsystem01.user.service;

import com.sliit.vehiclerentalsystem01.user.model.SupportStaff;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Reads and updates pending staff/admin access requests. Primary file is
 * {@code <data.directory>/staff_access_requests.txt} (where {@code UserAuthController} writes new
 * requests); the legacy {@code ~/RentalData/pending_admins.txt} is also read for backward
 * compatibility but never written to.
 */
@Service
public class MasterAdminPendingService {

    static final String PRIMARY_FILE = "staff_access_requests.txt";

    private final UserService userService;

    @Value("${app.data.directory:data}")
    private String dataDirectory;

    public MasterAdminPendingService(UserService userService) {
        this.userService = userService;
    }

    private Path primaryPath() {
        return Paths.get(dataDirectory).toAbsolutePath().normalize().resolve(PRIMARY_FILE);
    }

    private Path legacyPath() {
        return Paths.get(System.getProperty("user.home"), "RentalData", "pending_admins.txt");
    }

    public List<String> readPendingLines() {
        List<String> lines = new ArrayList<>();
        lines.addAll(readLines(primaryPath()));
        lines.addAll(readLines(legacyPath()));
        return Collections.unmodifiableList(lines);
    }

    private List<String> readLines(Path file) {
        if (file == null || !Files.isRegularFile(file)) {
            return List.of();
        }
        try {
            return Files.readAllLines(file, StandardCharsets.UTF_8).stream()
                    .map(String::trim)
                    .filter(s -> !s.isEmpty() && !s.startsWith("#"))
                    .toList();
        } catch (IOException e) {
            return List.of();
        }
    }

    /**
     * Removes a single line from the primary requests file. The legacy file is left untouched.
     * Returns {@code true} when at least one matching line was removed.
     */
    public boolean removeLine(String exactLine) {
        if (exactLine == null) {
            return false;
        }
        Path file = primaryPath();
        if (!Files.isRegularFile(file)) {
            return false;
        }
        try {
            List<String> kept = new ArrayList<>();
            boolean removed = false;
            for (String raw : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                String trimmed = raw == null ? "" : raw.trim();
                if (!removed && trimmed.equals(exactLine.trim())) {
                    removed = true;
                    continue;
                }
                kept.add(raw);
            }
            if (removed) {
                Files.write(file, kept, StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }
            return removed;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Promotes a pending request line into a fresh {@link SupportStaff} account, then removes
     * the line from the primary requests file. The temporary password is the result.
     */
    public String approve(String pendingLine) {
        String username = parseUsername(pendingLine);
        if (username.isEmpty()) {
            throw new IllegalArgumentException("invalid_request");
        }
        if (userService.findByUsername(username).isPresent()) {
            removeLine(pendingLine);
            throw new IllegalStateException("username_taken");
        }
        String tempPassword = "temp-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        String id = "EMP-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        SupportStaff staff = new SupportStaff(id, username, "", "", username, tempPassword);
        userService.save(staff);
        removeLine(pendingLine);
        return tempPassword;
    }

    /** Extracts a {@code username} component from a pending line in either CSV or label format. */
    public static String parseUsername(String line) {
        if (line == null) {
            return "";
        }
        String t = line.trim();
        if (t.isEmpty()) {
            return "";
        }
        Map<String, String> kv = new LinkedHashMap<>();
        for (String token : t.split("[,;|]")) {
            String tok = token.trim();
            if (tok.isEmpty()) {
                continue;
            }
            int eq = tok.indexOf('=');
            if (eq > 0) {
                kv.put(tok.substring(0, eq).trim().toLowerCase(), tok.substring(eq + 1).trim());
            }
        }
        if (kv.containsKey("username")) {
            return kv.get("username");
        }
        String[] parts = t.split(",");
        if (parts.length >= 2) {
            return parts[1].trim();
        }
        return parts[0].trim();
    }
}
