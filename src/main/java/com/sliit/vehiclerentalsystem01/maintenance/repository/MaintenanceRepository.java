package com.sliit.vehiclerentalsystem01.maintenance.repository;

import com.sliit.vehiclerentalsystem01.core.exceptions.StorageException;
import com.sliit.vehiclerentalsystem01.maintenance.entity.MaintenanceEntry;
import com.sliit.vehiclerentalsystem01.maintenance.entity.MaintenanceRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Repository for maintenance file storage.
 */
@Repository
public class MaintenanceRepository {

    private static final String FLEET_FILE_NAME = "maintenance-fleet.txt";
    private static final String LOG_FILE_NAME = "maintenance-log.txt";
    private static final String LEGACY_FILE_NAME = "maintenance.txt";
    private static final String DELIMITER = "\\|";
    private static final int LEGACY_MIN_FIELD_COUNT = 6;
    private static final int LEGACY_WITH_TYPE_FIELD_COUNT = 7;
    private static final int LEGACY_COMBINED_FIELD_COUNT = 10;
    private static final int FLEET_FIELD_COUNT = 8;
    private static final int LOG_FIELD_COUNT = 5;
    private static final Logger log = LoggerFactory.getLogger(MaintenanceRepository.class);
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();

    @Value("${app.data.directory:data}")
    private String dataDirectory;

    // Resolve file path in data directory
    private Path dataPath(String fileName) {
        Path resolvedPath = Paths.get(dataDirectory)
                                 .toAbsolutePath()
                                 .normalize()
                                 .resolve(fileName);
        return resolvedPath;
    }

    /** Find all vehicle records. */
    public List<MaintenanceRecord> findAllVehicles() {
        readLock.lock();
        try {
            Path fleetFilePath = dataPath(FLEET_FILE_NAME);
            boolean fleetFileExists = Files.exists(fleetFilePath);
            List<MaintenanceRecord> vehicles = readVehiclesFromDiskUnlocked();

            // Load legacy data if current file does not exist
            if (!fleetFileExists && vehicles.isEmpty()) {
                vehicles = readLegacyVehiclesFromDiskUnlocked();
            }

            return vehicles;
        } finally {
            readLock.unlock();
        }
    }

    /** Find all entry records. */
    public List<MaintenanceEntry> findAllEntries() {
        readLock.lock();
        try {
            List<MaintenanceEntry> entries = readEntriesFromDiskUnlocked();
            return entries;
        } finally {
            readLock.unlock();
        }
    }

    /** Save or update a vehicle record. */
    public void saveVehicle(MaintenanceRecord record) {
        writeLock.lock();
        try {
            List<MaintenanceRecord> records = readVehiclesFromDiskUnlocked();
            String vehicleIdToSave = record.getVehicleId();
            records.removeIf(existingRecord -> vehicleIdToSave.equals(existingRecord.getVehicleId()));
            records.add(record);
            writeVehiclesToDiskUnlocked(records);
        } finally {
            writeLock.unlock();
        }
    }

    /** Save a maintenance log entry. */
    public void saveEntry(MaintenanceEntry entry) {
        writeLock.lock();
        try {
            List<MaintenanceEntry> entries = readEntriesFromDiskUnlocked();
            entries.add(entry);
            writeEntriesToDiskUnlocked(entries);
        } finally {
            writeLock.unlock();
        }
    }

    /** Delete vehicle and its logs. */
    public void deleteVehicleByVehicleId(String vehicleId) {
        if (vehicleId == null || vehicleId.isBlank()) {
            return;
        }

        writeLock.lock();
        try {
            List<MaintenanceRecord> vehicles = readVehiclesFromDiskUnlocked();
            List<MaintenanceEntry> entries = readEntriesFromDiskUnlocked();
            vehicles.removeIf(record -> vehicleId.equals(record.getVehicleId()));
            entries.removeIf(entry -> vehicleId.equals(entry.getVehicleId()));
            writeVehiclesToDiskUnlocked(vehicles);
            writeEntriesToDiskUnlocked(entries);
        } finally {
            writeLock.unlock();
        }
    }

    /** Delete entry by ID. */
    public void deleteEntryById(String entryId) {
        if (entryId == null || entryId.isBlank()) {
            return;
        }

        writeLock.lock();
        try {
            List<MaintenanceEntry> entries = readEntriesFromDiskUnlocked();
            entries.removeIf(entry -> entryId.equals(entry.getId()));
            writeEntriesToDiskUnlocked(entries);
        } finally {
            writeLock.unlock();
        }
    }

    /** Save all vehicle records. */
    public void saveAllVehicles(List<MaintenanceRecord> records) {
        writeLock.lock();
        try {
            writeVehiclesToDiskUnlocked(records);
        } finally {
            writeLock.unlock();
        }
    }

    /** Save all entry records. */
    public void saveAllEntries(List<MaintenanceEntry> entries) {
        writeLock.lock();
        try {
            writeEntriesToDiskUnlocked(entries);
        } finally {
            writeLock.unlock();
        }
    }

    // Read vehicles from disk
    private List<MaintenanceRecord> readVehiclesFromDiskUnlocked() {
        Path path = dataPath(FLEET_FILE_NAME);
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }

        List<MaintenanceRecord> vehicles = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                try {
                    MaintenanceRecord record = parseVehicleLine(trimmed);
                    vehicles.add(record);
                } catch (RuntimeException ex) {
                    log.warn("Skipping invalid fleet line: {} - {}", trimmed, ex.getMessage());
                }
            }
            return vehicles;
        } catch (IOException e) {
            throw new StorageException("Maintenance fleet file operation failed", e);
        }
    }

    // Read log entries from disk
    private List<MaintenanceEntry> readEntriesFromDiskUnlocked() {
        Path path = dataPath(LOG_FILE_NAME);
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }

        List<MaintenanceEntry> entries = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                try {
                    MaintenanceEntry entry = parseEntryLine(trimmed);
                    entries.add(entry);
                } catch (RuntimeException ex) {
                    log.warn("Skipping invalid maintenance entry line: {} - {}", trimmed, ex.getMessage());
                }
            }
            return entries;
        } catch (IOException e) {
            throw new StorageException("Maintenance log file operation failed", e);
        }
    }

    // Read legacy records from disk
    private List<MaintenanceRecord> readLegacyVehiclesFromDiskUnlocked() {
        Path path = dataPath(LEGACY_FILE_NAME);
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }

        List<MaintenanceRecord> vehicles = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                try {
                    MaintenanceRecord record = parseLegacyVehicleLine(trimmed);
                    vehicles.add(record);
                } catch (RuntimeException ex) {
                    log.warn("Skipping invalid legacy maintenance line: {} - {}", trimmed, ex.getMessage());
                }
            }
            return vehicles;
        } catch (IOException e) {
            throw new StorageException("Legacy maintenance file operation failed", e);
        }
    }

    // Write vehicles to disk
    private void writeVehiclesToDiskUnlocked(List<MaintenanceRecord> records) {
        Path path = dataPath(FLEET_FILE_NAME);
        try {
            Files.createDirectories(path.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(
                    path,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING)) {
                writer.write("# id|vehicleId|vehicleType|vehicleModel|mileage|photoFileName|lastServiceDate|nextServiceDate");
                writer.newLine();
                for (MaintenanceRecord record : records) {
                    String lineContent = formatVehicleLine(record);
                    writer.write(lineContent);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new StorageException("Maintenance fleet file operation failed", e);
        }
    }

    // Write log entries to disk
    private void writeEntriesToDiskUnlocked(List<MaintenanceEntry> entries) {
        Path path = dataPath(LOG_FILE_NAME);
        try {
            Files.createDirectories(path.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(
                    path,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING)) {
                writer.write("# id|vehicleId|reason|maintenanceDate|notes");
                writer.newLine();
                for (MaintenanceEntry entry : entries) {
                    String lineContent = formatEntryLine(entry);
                    writer.write(lineContent);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new StorageException("Maintenance log file operation failed", e);
        }
    }

    // Parse fleet file line
    private MaintenanceRecord parseVehicleLine(String line) {
        String[] parts = line.split(DELIMITER, -1);
        if (parts.length < FLEET_FIELD_COUNT) {
            throw new IllegalArgumentException("Expected " + FLEET_FIELD_COUNT + " fields, got " + parts.length);
        }

        String id = nullSafeTrim(parts[0]);
        String vehicleId = nullSafeTrim(parts[1]);
        String vehicleType = nullSafeTrim(parts[2]);
        String vehicleModel = nullSafeTrim(parts[3]);
        String mileage = nullSafeTrim(parts[4]);
        String photoFileName = nullSafeTrim(parts[5]);
        LocalDate lastServiceDate = parseDate(parts[6]);
        LocalDate nextServiceDate = parseDate(parts[7]);

        if (id.isEmpty() || vehicleId.isEmpty()) {
            throw new IllegalArgumentException("id and vehicleId are required");
        }

        MaintenanceRecord record = new MaintenanceRecord(
                id,
                vehicleId,
                vehicleType,
                vehicleModel,
                mileage,
                photoFileName,
                lastServiceDate,
                nextServiceDate
        );
        return record;
    }

    // Parse log file line
    private MaintenanceEntry parseEntryLine(String line) {
        String[] parts = line.split(DELIMITER, -1);
        if (parts.length < LOG_FIELD_COUNT) {
            throw new IllegalArgumentException("Expected " + LOG_FIELD_COUNT + " fields, got " + parts.length);
        }

        String id = nullSafeTrim(parts[0]);
        String vehicleId = nullSafeTrim(parts[1]);
        String reason = nullSafeTrim(parts[2]);
        LocalDate maintenanceDate = parseDate(parts[3]);
        String notes = nullSafeTrim(parts[4]);

        if (id.isEmpty() || vehicleId.isEmpty()) {
            throw new IllegalArgumentException("id and vehicleId are required");
        }

        MaintenanceEntry entry = new MaintenanceEntry(
                id,
                vehicleId,
                reason,
                maintenanceDate,
                notes
        );
        return entry;
    }

    // Parse legacy vehicle line
    private MaintenanceRecord parseLegacyVehicleLine(String line) {
        String[] parts = line.split(DELIMITER, -1);
        if (parts.length < LEGACY_MIN_FIELD_COUNT) {
            throw new IllegalArgumentException("Expected at least " + LEGACY_MIN_FIELD_COUNT + " fields, got " + parts.length);
        }

        // Handles combined legacy format
        if (parts.length >= LEGACY_COMBINED_FIELD_COUNT) {
            String id = nullSafeTrim(parts[0]);
            String vehicleId = nullSafeTrim(parts[1]);
            String vehicleType = nullSafeTrim(parts[2]);
            String vehicleModel = nullSafeTrim(parts[3]);
            String mileage = nullSafeTrim(parts[4]);
            String photoFileName = nullSafeTrim(parts[5]);
            LocalDate lastServiceDate = parseDate(parts[6]);
            LocalDate nextServiceDate = parseDate(parts[7]);

            MaintenanceRecord record = new MaintenanceRecord(
                    id,
                    vehicleId,
                    vehicleType,
                    vehicleModel,
                    mileage,
                    photoFileName,
                    lastServiceDate,
                    nextServiceDate
            );
            return record;
        }

        // Handles shorter legacy formats
        boolean hasVehicleType = parts.length >= LEGACY_WITH_TYPE_FIELD_COUNT;
        String id = nullSafeTrim(parts[0]);
        String vehicleId = nullSafeTrim(parts[1]);
        String vehicleType = hasVehicleType ? nullSafeTrim(parts[2]) : "";
        String vehicleModel = "";
        String mileage = "";
        String photoFileName = "";
        String lastServiceDateRaw = hasVehicleType ? parts[4] : parts[3];
        String nextServiceDateRaw = hasVehicleType ? parts[5] : parts[4];
        LocalDate lastServiceDate = parseDate(lastServiceDateRaw);
        LocalDate nextServiceDate = parseDate(nextServiceDateRaw);

        MaintenanceRecord record = new MaintenanceRecord(
                id,
                vehicleId,
                vehicleType,
                vehicleModel,
                mileage,
                photoFileName,
                lastServiceDate,
                nextServiceDate
        );
        return record;
    }

    // Format vehicle record to delimited line
    private String formatVehicleLine(MaintenanceRecord record) {
        String serializedLine = String.join("|",
                storageSafe(record.getId()),
                storageSafe(record.getVehicleId()),
                storageSafe(record.getVehicleType()),
                storageSafe(record.getVehicleModel()),
                storageSafe(record.getMileage()),
                storageSafe(record.getPhotoFileName()),
                formatDate(record.getLastServiceDate()),
                formatDate(record.getNextServiceDate())
        );
        return serializedLine;
    }

    // Format log entry to delimited line
    private String formatEntryLine(MaintenanceEntry entry) {
        String serializedLine = String.join("|",
                storageSafe(entry.getId()),
                storageSafe(entry.getVehicleId()),
                storageSafe(entry.getReason()),
                formatDate(entry.getMaintenanceDate()),
                storageSafe(entry.getNotes())
        );
        return serializedLine;
    }

    // Parse date safely
    private static LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        LocalDate date = LocalDate.parse(value.trim());
        return date;
    }

    // Format date safely
    private static String formatDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        String dateString = date.toString();
        return dateString;
    }

    // Trim string safely
    private static String nullSafeTrim(String value) {
        if (value == null) {
            return "";
        }
        String trimmed = value.trim();
        return trimmed;
    }

    // Safe string wrapper
    private static String nullSafe(String value) {
        if (value == null) {
            return "";
        }
        return value;
    }

    // Escape delimiter character
    private static String storageSafe(String value) {
        String sanitized = nullSafe(value).replace("|", "/");
        return sanitized;
    }
}
