package com.sliit.vehiclerentalsystem01.maintenance.service;

import com.sliit.vehiclerentalsystem01.maintenance.model.MaintenanceRecord;
import com.sliit.vehiclerentalsystem01.maintenance.model.MaintenanceStatus;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MaintenanceService {

    private static final String FILE_NAME = "maintenance.txt";
    private static final String DELIMITER = "\\|";
    private static final int FIELD_COUNT = 6;
    private static final Pattern ID_PATTERN = Pattern.compile("^MNT-(\\d+)$");

    @Value("${app.data.directory:data}")
    private String dataDirectory;

    private final List<MaintenanceRecord> records = new ArrayList<>();

    @PostConstruct
    void init() {
        loadFromFile();
        if (records.isEmpty()) {
            seedDefaults();
            saveToFile();
        }
    }

    // --- Public read API (unchanged) ---

    public List<MaintenanceRecord> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(records));
    }

    public List<MaintenanceRecord> findAvailableVehicles() {
        return records.stream()
                .filter(r -> statusMatches(r, MaintenanceStatus.AVAILABLE))
                .toList();
    }

    public List<MaintenanceRecord> findInMaintenance() {
        return records.stream()
                .filter(r -> statusMatches(r, MaintenanceStatus.MAINTENANCE))
                .toList();
    }

    public List<MaintenanceRecord> findInUse() {
        return records.stream()
                .filter(r -> statusMatches(r, MaintenanceStatus.IN_USE))
                .toList();
    }

    public int countAvailable() {
        return findAvailableVehicles().size();
    }

    public int countInMaintenance() {
        return findInMaintenance().size();
    }

    public int countInUse() {
        return findInUse().size();
    }

    public Optional<MaintenanceRecord> findByVehicleId(String vehicleId) {
        if (vehicleId == null || vehicleId.isBlank()) {
            return Optional.empty();
        }
        String key = vehicleId.trim();
        return records.stream()
                .filter(r -> key.equals(r.getVehicleId()))
                .findFirst();
    }

    // --- CRUD ---

    public void addRecord(MaintenanceRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("Maintenance record must not be null");
        }
        if (record.getVehicleId() == null || record.getVehicleId().isBlank()) {
            throw new IllegalArgumentException("vehicleId is required");
        }
        String vehicleId = record.getVehicleId().trim();
        if (findByVehicleId(vehicleId).isPresent()) {
            throw new IllegalArgumentException("A maintenance record already exists for vehicle: " + vehicleId);
        }
        if (record.getId() == null || record.getId().isBlank()) {
            record.setId(nextMaintenanceId());
        }
        if (record.getStatus() != null) {
            validateStatus(record.getStatus());
            record.setStatus(record.getStatus().trim());
        }
        record.setVehicleId(vehicleId);
        records.add(record);
        persist();
    }

    public void updateStatus(String vehicleId, String status) {
        if (vehicleId == null || vehicleId.isBlank()) {
            throw new IllegalArgumentException("vehicleId is required");
        }
        validateStatus(status);
        MaintenanceRecord record = findByVehicleId(vehicleId.trim())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No maintenance record found for vehicle: " + vehicleId.trim()));
        record.setStatus(status.trim());
        persist();
    }

    public void deleteRecord(String vehicleId) {
        if (vehicleId == null || vehicleId.isBlank()) {
            return;
        }
        String key = vehicleId.trim();
        boolean removed = records.removeIf(r -> key.equals(r.getVehicleId()));
        if (removed) {
            persist();
        }
    }

    // --- File I/O ---

    private Path maintenancePath() {
        return Paths.get(dataDirectory).toAbsolutePath().normalize().resolve(FILE_NAME);
    }

    private void loadFromFile() {
        records.clear();
        Path path = maintenancePath();
        if (!Files.exists(path)) {
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                try {
                    MaintenanceRecord record = parseLine(trimmed);
                    if (record != null) {
                        records.add(record);
                    }
                } catch (RuntimeException ex) {
                    System.err.println("Skipping invalid maintenance line: " + trimmed + " — " + ex.getMessage());
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read maintenance file: " + path, e);
        }
    }

    private void saveToFile() {
        Path path = maintenancePath();
        try {
            Files.createDirectories(path.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(
                    path,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING)) {
                writer.write("# id|vehicleId|status|lastServiceDate|nextServiceDate|notes");
                writer.newLine();
                for (MaintenanceRecord record : records) {
                    writer.write(formatLine(record));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write maintenance file: " + path, e);
        }
    }

    private void persist() {
        saveToFile();
    }

    // --- Parse / format ---

    private MaintenanceRecord parseLine(String line) {
        String[] parts = line.split(DELIMITER, -1);
        if (parts.length < FIELD_COUNT) {
            throw new IllegalArgumentException("Expected " + FIELD_COUNT + " fields, got " + parts.length);
        }
        String id = nullSafeTrim(parts[0]);
        String vehicleId = nullSafeTrim(parts[1]);
        String status = nullSafeTrim(parts[2]);
        LocalDate lastServiceDate = parseDate(parts[3]);
        LocalDate nextServiceDate = parseDate(parts[4]);
        String notes = parts.length > 5 ? nullSafeTrim(parts[5]) : "";
        if (id.isEmpty() || vehicleId.isEmpty()) {
            throw new IllegalArgumentException("id and vehicleId are required");
        }
        return new MaintenanceRecord(id, vehicleId, status, lastServiceDate, nextServiceDate, notes);
    }

    private String formatLine(MaintenanceRecord record) {
        return String.join("|",
                nullSafe(record.getId()),
                nullSafe(record.getVehicleId()),
                nullSafe(record.getStatus()),
                formatDate(record.getLastServiceDate()),
                formatDate(record.getNextServiceDate()),
                nullSafe(record.getNotes()));
    }

    private static LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return LocalDate.parse(value.trim());
    }

    private static String formatDate(LocalDate date) {
        return date == null ? "" : date.toString();
    }

    private static String nullSafeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    private static String nullSafe(String value) {
        return value == null ? "" : value;
    }

    // --- Bootstrap & helpers ---

    private void seedDefaults() {
        LocalDate today = LocalDate.now();
        records.clear();
        records.add(new MaintenanceRecord(
                "MNT-001",
                "VEH-101",
                MaintenanceStatus.AVAILABLE,
                today.minusMonths(2),
                today.plusMonths(4),
                "Routine inspection completed; ready for rental."));
        records.add(new MaintenanceRecord(
                "MNT-002",
                "VEH-102",
                MaintenanceStatus.IN_USE,
                today.minusMonths(1),
                today.plusMonths(5),
                "Currently on active booking."));
        records.add(new MaintenanceRecord(
                "MNT-003",
                "VEH-103",
                MaintenanceStatus.MAINTENANCE,
                today.minusMonths(3),
                today.minusDays(14),
                "Brake pads replacement in progress."));
        records.add(new MaintenanceRecord(
                "MNT-004",
                "VEH-104",
                MaintenanceStatus.AVAILABLE,
                today.minusWeeks(3),
                today.plusMonths(3),
                "Oil change and tire rotation done."));
        records.add(new MaintenanceRecord(
                "MNT-005",
                "VEH-105",
                MaintenanceStatus.IN_USE,
                today.minusMonths(4),
                today.plusMonths(2),
                "Long-term corporate rental."));
    }

    private String nextMaintenanceId() {
        int max = 0;
        for (MaintenanceRecord record : records) {
            if (record.getId() == null) {
                continue;
            }
            Matcher matcher = ID_PATTERN.matcher(record.getId().trim());
            if (matcher.matches()) {
                max = Math.max(max, Integer.parseInt(matcher.group(1)));
            }
        }
        return String.format("MNT-%03d", max + 1);
    }

    private static void validateStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("status is required");
        }
        String value = status.trim();
        if (!MaintenanceStatus.AVAILABLE.equals(value)
                && !MaintenanceStatus.IN_USE.equals(value)
                && !MaintenanceStatus.MAINTENANCE.equals(value)) {
            throw new IllegalArgumentException("Invalid status: " + value);
        }
    }

    private static boolean statusMatches(MaintenanceRecord record, String status) {
        return record != null
                && record.getStatus() != null
                && status.equals(record.getStatus());
    }
}
