package com.sliit.vehiclerentalsystem01.user.service;

import com.sliit.vehiclerentalsystem01.booking.service.BookingService;
import com.sliit.vehiclerentalsystem01.user.model.AdminStaff;
import com.sliit.vehiclerentalsystem01.user.model.Customer;
import com.sliit.vehiclerentalsystem01.user.model.Staff;
import com.sliit.vehiclerentalsystem01.user.model.SupportStaff;
import com.sliit.vehiclerentalsystem01.user.model.User;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserFileServiceImpl implements UserService {

    private static final String FILE_NAME = "users.txt";

    private static final Logger log = LoggerFactory.getLogger(UserFileServiceImpl.class);

    private final BookingService bookingService;

    @Value("${app.data.directory:data}")
    private String dataDirectory;

    /**
     * BookingService is {@link Lazy} to break the cycle: BookingFileServiceImpl itself depends
     * on UserService.
     */
    public UserFileServiceImpl(@Lazy BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostConstruct
    void logUserStoreLocation() {
        log.info("User store file ({}): {}", FILE_NAME, usersPath());
    }

    /** Absolute path to {@value #FILE_NAME} (under {@code app.data.directory}); used as the user store for every read/write. */
    private Path usersPath() {
        return Paths.get(dataDirectory).toAbsolutePath().normalize().resolve(FILE_NAME);
    }

    @Override
    public List<User> findAll() {
        return readAllUsers();
    }

    @Override
    public Optional<User> findById(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        return readAllUsers().stream().filter(u -> id.equals(u.getId())).findFirst();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        if (username == null || username.isBlank()) {
            return Optional.empty();
        }
        String key = username.trim();
        return readAllUsers().stream()
                .filter(u -> {
                    if (u instanceof Customer c) {
                        return key.equalsIgnoreCase(c.getUsername());
                    }
                    if (u instanceof Staff s) {
                        return key.equalsIgnoreCase(s.getUsername());
                    }
                    return false;
                })
                .findFirst();
    }

    @Override
    public List<Customer> findAllCustomers() {
        return readAllUsers().stream()
                .filter(u -> u instanceof Customer)
                .map(u -> (Customer) u)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<Staff> findAllStaff() {
        return readAllUsers().stream()
                .filter(u -> u instanceof Staff)
                .map(u -> (Staff) u)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public void save(User user) {
        if (user.getId() == null || user.getId().isBlank()) {
            user.setId(UUID.randomUUID().toString());
        }
        List<User> users = readAllUsers();
        users.removeIf(u -> user.getId().equals(u.getId()));
        users.add(user);
        writeAll(users);
    }

    /**
     * Refuses to delete a customer whose username still has linked bookings. Throws
     * {@link IllegalStateException} with code {@code customer_in_use} so controllers can surface
     * a friendly error. Staff deletions are unaffected.
     */
    @Override
    public void deleteById(String id) {
        if (id == null || id.isBlank()) {
            return;
        }
        findById(id)
                .filter(u -> u instanceof Customer)
                .map(u -> (Customer) u)
                .ifPresent(c -> {
                    if (!bookingService.findBookingsLinkedToCustomer(c).isEmpty()) {
                        throw new IllegalStateException("customer_in_use");
                    }
                });

        List<User> users = readAllUsers().stream()
                .filter(u -> !id.equals(u.getId()))
                .collect(Collectors.toCollection(ArrayList::new));
        writeAll(users);
    }

    /**
     * Validates credentials against the latest contents of {@value #FILE_NAME} on disk.
     * Customers may sign in with username or email (case-insensitive); staff match username only.
     * Each call re-reads the file (no cached user list), so edits apply on the next login attempt.
     */
    @Override
    public Optional<User> authenticate(String username, String password) {
        if (username == null || password == null) {
            return Optional.empty();
        }
        String u = username.trim();
        String p = password.trim();
        if (u.isEmpty() || p.isEmpty()) {
            return Optional.empty();
        }
        return readAllUsers().stream()
                .filter(user -> {
                    String storedUser = "";
                    String storedPass = "";
                    if (user instanceof Customer c) {
                        storedUser = c.getUsername() == null ? "" : c.getUsername().trim();
                        String storedEmail = c.getEmail() == null ? "" : c.getEmail().trim();
                        storedPass = c.getPassword() == null ? "" : c.getPassword().trim();
                        boolean idMatches = u.equalsIgnoreCase(storedUser) || u.equalsIgnoreCase(storedEmail);
                        return idMatches && p.equals(storedPass);
                    } else if (user instanceof Staff s) {
                        storedUser = s.getUsername() == null ? "" : s.getUsername().trim();
                        storedPass = s.getPassword() == null ? "" : s.getPassword().trim();
                    }
                    return u.equalsIgnoreCase(storedUser) && p.equals(storedPass);
                })
                .findFirst();
    }

    /** Reads all customer and staff rows from {@link #usersPath()} (full reload every time). */
    private List<User> readAllUsers() {
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
            throw new IllegalStateException("Failed to read users file: " + path, e);
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

    private void writeAll(List<User> users) {
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
            throw new IllegalStateException("Failed to write users file: " + path, e);
        }
    }
}
