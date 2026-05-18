package com.sliit.vehiclerentalsystem01.maintenance.repository;

import com.sliit.vehiclerentalsystem01.core.exceptions.StorageException;
import com.sliit.vehiclerentalsystem01.maintenance.entity.MaintenanceRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

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
import java.util.List;

@Repository
public class MaintenanceRepository {

    private static final String FILE_NAME = "maintenance.txt";
    private static final String DELIMITER = "\\|";
    private static final int FIELD_COUNT = 6;

    @Value("${app.data.directory:data}")
    private String dataDirectory;

    private Path maintenancePath() {
        return Paths.get(dataDirectory).toAbsolutePath().normalize().resolve(FILE_NAME);
    }

    public List<MaintenanceRecord> findAll() {
        List<MaintenanceRecord> records = new ArrayList<>();
        Path path = maintenancePath();
        if (!Files.exists(path)) {
            return records;
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
            return records;
        } catch (IOException e) {
            throw new StorageException("Maintenance repository file operation failed", e);
        }
    }

    public void saveAll(List<MaintenanceRecord> records) {
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
            throw new StorageException("Maintenance repository file operation failed", e);
        }
    }

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
}
