package com.sliit.vehiclerentalsystem01.user.repository;

import com.sliit.vehiclerentalsystem01.core.exceptions.StorageException;
import com.sliit.vehiclerentalsystem01.user.entity.AdminStaff;
import com.sliit.vehiclerentalsystem01.user.entity.Customer;
import com.sliit.vehiclerentalsystem01.user.entity.Staff;
import com.sliit.vehiclerentalsystem01.user.entity.SupportStaff;
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
import java.util.stream.Collectors;

@Repository
public class UserRepository {

    private static final String FILE_NAME = "users.txt";

    private static final Logger log = LoggerFactory.getLogger(UserRepository.class);

    @Value("${app.data.directory:data}")
    private String dataDirectory;

    @PostConstruct
    void logUserStoreLocation() {
        log.info("User store file ({}): {}", FILE_NAME, usersPath());
    }

    /** Absolute path to {@value #FILE_NAME} (under {@code app.data.directory}). */
    private Path usersPath() {
        return Paths.get(dataDirectory).toAbsolutePath().normalize().resolve(FILE_NAME);
    }

    public List<User> findAll() {
        Path path = usersPath();
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }
        try {
            return Files.readAllLines(path, StandardCharsets.UTF_8).stream()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                    .map(this::parseLine)
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            throw new StorageException("Database file operation failed", e);
        }
    }

    public void saveAll(List<User> users) {
        Path path = usersPath();
        try {
            Files.createDirectories(path.getParent());
            List<String> lines = new ArrayList<>();
            lines.add("# Customer: id,name,email,username,password,nic,license,phone (8 CSV fields)");
            lines.add("# Staff: id,name,email,phone,username,password,role (7 CSV fields)");
            lines.addAll(users.stream().map(this::toLine).toList());
            Files.write(path, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new StorageException("Database file operation failed", e);
        }
    }

    private User parseLine(String line) {
        if (line.contains("|")) {
            return parseLegacyPipeLine(line);
        }
        String[] a = line.split(",", -1);
        if (a.length == 8) {
            Customer c = Customer.fromCSV(line);
            if (c != null) {
                return c;
            }
        }
        if (a.length == 7) {
            return parseStaffCsv(a);
        }
        throw new IllegalArgumentException("Invalid user record: " + line);
    }

    /** Backward compatibility for older pipe files. */
    private User parseLegacyPipeLine(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length < 7) {
            throw new IllegalArgumentException("Invalid user record: " + line);
        }
        return switch (p[0]) {
            case "CUSTOMER" -> {
                Customer c = new Customer(
                        p[1],
                        p.length >= 10 ? p[7] : "",
                        p[4],
                        p[2],
                        p[3],
                        p.length >= 10 ? p[8] : "N/A",
                        p[6],
                        p[5]);
                yield c;
            }
            case "STAFF" -> {
                String role = p.length >= 10 ? p[9].trim() : "SUPPORT";
                yield parseStaffFromPipe(p, role);
            }
            default -> throw new IllegalArgumentException("Unknown user type: " + p[0]);
        };
    }

    private Staff parseStaffFromPipe(String[] p, String role) {
        String id = p[1];
        String username = p[2];
        String password = p[3];
        String email = p[4];
        String name = p.length >= 10 ? p[7] : username;
        String phone = p.length >= 10 ? p[8] : "";
        if (role.equalsIgnoreCase("ADMIN") || role.equalsIgnoreCase("Admin")) {
            return new AdminStaff(id, name, email, phone, username, password);
        }
        return new SupportStaff(id, name, email, phone, username, password);
    }

    private Staff parseStaffCsv(String[] a) {
        String id = a[0].trim();
        String name = a[1].trim();
        String email = a[2].trim();
        String phone = a[3].trim();
        String username = a[4].trim();
        String password = a[5].trim();
        String role = a[6].trim();
        if ("Admin".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role)) {
            return new AdminStaff(id, name, email, phone, username, password);
        }
        return new SupportStaff(id, name, email, phone, username, password);
    }

    private String toLine(User user) {
        if (user instanceof Customer c) {
            return c.toCSV();
        }
        if (user instanceof Staff s) {
            return String.join(",",
                    safe(s.getId()),
                    safe(s.getName()),
                    safe(s.getEmail()),
                    safe(s.getPhone()),
                    safe(s.getUsername()),
                    safe(s.getPassword()),
                    safe(s.getRole()).isEmpty() ? "Support" : s.getRole());
        }
        throw new IllegalArgumentException("Unsupported user type: " + user.getClass());
    }

    private static String safe(String v) {
        return v == null ? "" : v;
    }
}
