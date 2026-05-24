package com.sliit.vehiclerentalsystem01.user.service;

import com.sliit.vehiclerentalsystem01.core.io.FileDbHelper;
import com.sliit.vehiclerentalsystem01.user.entity.SupportStaff;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

// Pending staff sign-up requests for master admin
@Service
public class MasterAdminPendingService {

    static final String PRIMARY_FILE = "staff_access_requests.txt";

    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.data.directory:data}")
    private String dataDirectory;

    public MasterAdminPendingService(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    private Path primaryPath() {
        return Paths.get(dataDirectory).toAbsolutePath().normalize().resolve(PRIMARY_FILE);
    }

    private Path legacyPath() {
        return Paths.get(System.getProperty("user.home"), "RentalData", "pending_admins.txt");
    }

    // Read pending request lines
    public List<String> readPendingLines() {
        readLock.lock();
        try {
            List<String> lines = new ArrayList<>();
            lines.addAll(readLinesUnlocked(primaryPath()));
            lines.addAll(readLinesUnlocked(legacyPath()));
            return Collections.unmodifiableList(lines);
        } finally {
            readLock.unlock();
        }
    }

    // Add a new staff access request
    public void appendStaffAccessRequestLine(String username) throws IOException {
        if (username == null || username.isBlank()) {
            throw new IOException("username required");
        }
        writeLock.lock();
        try {
            Path file = primaryPath();
            List<String> lines = new ArrayList<>();
            if (Files.exists(file)) {
                lines.addAll(Files.readAllLines(file, StandardCharsets.UTF_8));
            }
            String line = Instant.now().toString() + "," + username.trim();
            lines.add(line);
            FileDbHelper.writeAtomic(file, lines);
        } finally {
            writeLock.unlock();
        }
    }

    private List<String> readLinesUnlocked(Path file) {
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

    public boolean removeLine(String exactLine) {
        if (exactLine == null) {
            return false;
        }
        writeLock.lock();
        try {
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
                    FileDbHelper.writeAtomic(file, kept);
                }
                return removed;
            } catch (IOException e) {
                return false;
            }
        } finally {
            writeLock.unlock();
        }
    }

    // Approve request and create support staff account
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
        String email = username.toLowerCase() + "@staff.driveaway.local";
        SupportStaff staff = new SupportStaff(id, username, email, "", username, passwordEncoder.encode(tempPassword));
        userService.save(staff);
        removeLine(pendingLine);
        return tempPassword;
    }

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
