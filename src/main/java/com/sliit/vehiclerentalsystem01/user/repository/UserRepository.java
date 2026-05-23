package com.sliit.vehiclerentalsystem01.user.repository;

import com.sliit.vehiclerentalsystem01.core.exceptions.StorageException;
import com.sliit.vehiclerentalsystem01.user.entity.Customer;
import com.sliit.vehiclerentalsystem01.user.entity.Staff;
import com.sliit.vehiclerentalsystem01.user.entity.User;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

// Reads and writes users from data/users.txt
@Repository
public class UserRepository {

    private static final String FILE_NAME = "users.txt";

    private static final Logger log = LoggerFactory.getLogger(UserRepository.class);

    // Many readers (login/list), exclusive writer (save/delete)
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();

    @Value("${app.data.directory:data}")
    private String dataDirectory;

    @PostConstruct
    void logUserStoreLocation() {
        log.info("User store file ({}): {}", FILE_NAME, usersPath());
    }

    private Path usersPath() {
        return Paths.get(dataDirectory).toAbsolutePath().normalize().resolve(FILE_NAME);
    }

    // Load all users from file
    public List<User> findAll() {
        readLock.lock();
        try {
            return readUsersFromDiskUnlocked();
        } finally {
            readLock.unlock();
        }
    }

    // Save or update one user in the file
    public void saveUser(User user) {
        writeLock.lock();
        try {
            List<User> users = readUsersFromDiskUnlocked();
            users.removeIf(u -> user.getId().equals(u.getId()));
            users.add(user);
            writeUsersToDiskUnlocked(users);
        } finally {
            writeLock.unlock();
        }
    }

    // Delete user by id
    public void deleteUserById(String id) {
        if (id == null || id.isBlank()) {
            return;
        }
        writeLock.lock();
        try {
            List<User> users = readUsersFromDiskUnlocked();
            users.removeIf(u -> id.equals(u.getId()));
            writeUsersToDiskUnlocked(users);
        } finally {
            writeLock.unlock();
        }
    }

    public void saveAll(List<User> users) {
        writeLock.lock();
        try {
            writeUsersToDiskUnlocked(users);
        } finally {
            writeLock.unlock();
        }
    }

    // Read users from file (caller must hold lock)
    private List<User> readUsersFromDiskUnlocked() {
        Path path = usersPath();
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }
        try {
            List<User> result = new ArrayList<>();
            for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                try {
                    result.add(parseLine(trimmed));
                } catch (IllegalArgumentException ex) {
                    log.warn("Skipping malformed user line: {}", trimmed, ex);
                }
            }
            return result;
        } catch (IOException e) {
            throw new StorageException("Database file operation failed", e);
        }
    }

    // Write all users back to file
    private void writeUsersToDiskUnlocked(List<User> users) {
        Path path = usersPath();
        try {
            Files.createDirectories(path.getParent());
            List<String> lines = new ArrayList<>();
            lines.add("# Customer: id,name,email,username,password,nic,license,phone,profilePicture (9 CSV fields)");
            lines.add("# Staff: id,name,email,phone,username,password,role,profilePicture (8 CSV fields)");
            lines.addAll(users.stream().map(this::toLine).toList());
            Files.write(path, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new StorageException("Database file operation failed", e);
        }
    }

    // Turn one file line into a Customer or Staff object
    private User parseLine(String line) {
        if (line.contains("|")) {
            return parseLegacyPipeLine(line);
        }
        String[] a = line.split(",", -1);
        if (a.length == 9) {
            Customer c = Customer.fromCSV(line);
            if (c != null) {
                return c;
            }
        }
        if (a.length == 8 && isStaffRole(a[6])) {
            Staff staff = Staff.fromCSV(line);
            if (staff != null) {
                return staff;
            }
        }
        if (a.length == 8) {
            Customer c = Customer.fromCSV(line);
            if (c != null) {
                return c;
            }
        }
        if (a.length == 7) {
            Staff staff = Staff.fromCSV(line);
            if (staff != null) {
                return staff;
            }
        }
        throw new IllegalArgumentException("Invalid user record: " + line);
    }

    private static boolean isStaffRole(String role) {
        if (role == null) {
            return false;
        }
        String trimmed = role.trim();
        return "Admin".equalsIgnoreCase(trimmed)
                || "ADMIN".equalsIgnoreCase(trimmed)
                || "Support".equalsIgnoreCase(trimmed)
                || "SUPPORT".equalsIgnoreCase(trimmed);
    }

    // Old pipe-separated format
    private User parseLegacyPipeLine(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length < 7) {
            throw new IllegalArgumentException("Invalid user record: " + line);
        }
        return switch (p[0]) {
            case "CUSTOMER" -> {
                String csvLine = String.join(",",
                        csvSafe(p[1]),
                        csvSafe(p.length >= 10 ? p[7] : ""),
                        csvSafe(p[4]),
                        csvSafe(p[2]),
                        csvSafe(p[3]),
                        csvSafe(p.length >= 10 ? p[8] : "N/A"),
                        csvSafe(p[6]),
                        csvSafe(p[5]));
                Customer c = Customer.fromCSV(csvLine);
                if (c == null) {
                    throw new IllegalArgumentException("Invalid user record: " + line);
                }
                yield c;
            }
            case "STAFF" -> {
                String role = p.length >= 10 ? p[9].trim() : "SUPPORT";
                String name = p.length >= 10 ? p[7] : p[2];
                String phone = p.length >= 10 ? p[8] : "";
                String csvLine = String.join(",",
                        csvSafe(p[1]),
                        csvSafe(name),
                        csvSafe(p[4]),
                        csvSafe(phone),
                        csvSafe(p[2]),
                        csvSafe(p[3]),
                        csvSafe(role));
                Staff staff = Staff.fromCSV(csvLine);
                if (staff == null) {
                    throw new IllegalArgumentException("Invalid user record: " + line);
                }
                yield staff;
            }
            default -> throw new IllegalArgumentException("Unknown user type: " + p[0]);
        };
    }

    private String toLine(User user) {
        return user.toCSV();
    }

    private static String csvSafe(String value) {
        return value == null ? "" : value.replace(",", " ").trim();
    }
}
