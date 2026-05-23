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
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Repository managing file-based persistent storage of maintenance records and logs.
 * Uses a ReentrantReadWriteLock for thread safety.
 */
@Repository
public class MaintenanceRepository {

    /** File name for storing active vehicle fleet maintenance details. */
    private static final String FLEET_FILE_NAME = "maintenance-fleet.txt";

    /** File name for storing historical maintenance log entries. */
    private static final String LOG_FILE_NAME = "maintenance-log.txt";

    /** File name for legacy maintenance data. */
    private static final String LEGACY_FILE_NAME = "maintenance.txt";

    /** Delimiter pattern for parsing pipe-separated fields. */
    private static final String DELIMITER = "\\|";

    /** Minimum fields required in legacy format records. */
    private static final int LEGACY_MIN_FIELD_COUNT = 6;

    /** Field count for legacy records with vehicle type. */
    private static final int LEGACY_WITH_TYPE_FIELD_COUNT = 7;

    /** Field count for combined legacy records. */
    private static final int LEGACY_COMBINED_FIELD_COUNT = 10;

    /** Expected field count in the maintenance-fleet file. */
    private static final int FLEET_FIELD_COUNT = 8;

    /** Expected field count in the maintenance-log file. */
    private static final int LOG_FIELD_COUNT = 5;

    /** Logger instance. */
    private static final Logger log = LoggerFactory.getLogger(MaintenanceRepository.class);

    /** Read-write lock for thread safety. */
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    /** Shared read lock. */
    private final Lock readLock = rwLock.readLock();

    /** Exclusive write lock. */
    private final Lock writeLock = rwLock.writeLock();

    /** Configurable path to the data storage directory. */
    @Value("${app.data.directory:data}")
    private String dataDirectory;

    /** Resolves a file name to its absolute normalized path in the data directory. */
    private Path dataPath(String fileName) {
        Path resolvedPath = Paths.get(dataDirectory)
                                 .toAbsolutePath()
                                 .normalize()
                                 .resolve(fileName);
        return resolvedPath;
    }

    /** Retrieves all vehicle maintenance records, falling back to legacy data if needed. */
    public List<MaintenanceRecord> findAllVehicles() {
        // Acquire read lock
        readLock.lock();
        try {
            // Check if file exists
            Path fleetFilePath = dataPath(FLEET_FILE_NAME);
            boolean fleetFileExists = Files.exists(fleetFilePath);

            // Read standard records
            List<MaintenanceRecord> vehicles = readVehiclesFromDiskUnlocked();

            // Migrate legacy data if necessary
            if (!fleetFileExists && vehicles.isEmpty()) {
                vehicles = readLegacyVehiclesFromDiskUnlocked();
            }

            return vehicles;
        } finally {
            // Release read lock
            readLock.unlock();
        }
    }

    /** Retrieves all maintenance log entries from persistence. */
    public List<MaintenanceEntry> findAllEntries() {
        // Acquire read lock
        readLock.lock();
        try {
            List<MaintenanceEntry> entries = readEntriesFromDiskUnlocked();
            return entries;
        } finally {
            readLock.unlock();
        }
    }

    /** Saves or updates a vehicle maintenance record. */
    public void saveVehicle(MaintenanceRecord record) {
        // Acquire write lock
        writeLock.lock();
        try {
            // Read existing records
            List<MaintenanceRecord> records = readVehiclesFromDiskUnlocked();

            // Remove existing record for the same vehicle ID
            String vehicleIdToSave = record.getVehicleId();
            records.removeIf(existingRecord -> vehicleIdToSave.equals(existingRecord.getVehicleId()));

            // Add the new record
            records.add(record);

            // Save back to disk
            writeVehiclesToDiskUnlocked(records);
        } finally {
            writeLock.unlock();
        }
    }

    /** Appends a new maintenance entry to the logs. */
    public void saveEntry(MaintenanceEntry entry) {
        // Acquire write lock
        writeLock.lock();
        try {
            // Read current log entries
            List<MaintenanceEntry> entries = readEntriesFromDiskUnlocked();

            // Append new entry
            entries.add(entry);

            // Save back to disk
            writeEntriesToDiskUnlocked(entries);
        } finally {
            writeLock.unlock();
        }
    }

    /** Removes a vehicle and its associated entries by vehicle ID. */
    public void deleteVehicleByVehicleId(String vehicleId) {
        // Do nothing if ID is blank
        if (vehicleId == null || vehicleId.isBlank()) {
            return;
        }

        // Acquire write lock
        writeLock.lock();
        try {
            // Retrieve records
            List<MaintenanceRecord> vehicles = readVehiclesFromDiskUnlocked();
            List<MaintenanceEntry> entries = readEntriesFromDiskUnlocked();

            // Delete matching records
            vehicles.removeIf(record -> vehicleId.equals(record.getVehicleId()));
            entries.removeIf(entry -> vehicleId.equals(entry.getVehicleId()));

            // Persist changes
            writeVehiclesToDiskUnlocked(vehicles);
            writeEntriesToDiskUnlocked(entries);
        } finally {
            writeLock.unlock();
        }
    }

    /** Deletes a maintenance entry by its ID. */
    public void deleteEntryById(String entryId) {
        // Do nothing if ID is blank
        if (entryId == null || entryId.isBlank()) {
            return;
        }

        // Acquire write lock
        writeLock.lock();
        try {
            // Retrieve log entries
            List<MaintenanceEntry> entries = readEntriesFromDiskUnlocked();

            // Remove entry
            entries.removeIf(entry -> entryId.equals(entry.getId()));

            // Persist changes
            writeEntriesToDiskUnlocked(entries);
        } finally {
            writeLock.unlock();
        }
    }

    /** Overwrites all vehicle maintenance records on disk. */
    public void saveAllVehicles(List<MaintenanceRecord> records) {
        // Acquire write lock
        writeLock.lock();
        try {
            writeVehiclesToDiskUnlocked(records);
        } finally {
            writeLock.unlock();
        }
    }

    /** Overwrites all maintenance log entries on disk. */
    public void saveAllEntries(List<MaintenanceEntry> entries) {
        // Acquire write lock
        writeLock.lock();
        try {
            writeEntriesToDiskUnlocked(entries);
        } finally {
            writeLock.unlock();
        }
    }

    /** Reads vehicle records from disk (unlocked). */
    private List<MaintenanceRecord> readVehiclesFromDiskUnlocked() {
        Path path = dataPath(FLEET_FILE_NAME);

        // Return empty list if file doesn't exist
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }

        List<MaintenanceRecord> vehicles = new ArrayList<>();

        // Read file contents
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;

            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();

                // Skip blank lines and header comments
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }

                try {
                    MaintenanceRecord record = parseVehicleLine(trimmed);
                    vehicles.add(record);
                } catch (RuntimeException ex) {
                    // Skip and log invalid line
                    log.warn("Skipping invalid fleet line: {} - {}", trimmed, ex.getMessage());
                }
            }

            return vehicles;
        } catch (IOException e) {
            throw new StorageException("Maintenance fleet file operation failed", e);
        }
    }

    /** Reads maintenance entries from disk (unlocked). */
    private List<MaintenanceEntry> readEntriesFromDiskUnlocked() {
        Path path = dataPath(LOG_FILE_NAME);

        // Return empty list if file doesn't exist
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }

        List<MaintenanceEntry> entries = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;

            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();

                // Skip blank lines and header comments
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

    /** Reads legacy vehicle records from disk (unlocked). */
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

                // Skip blank lines and header comments
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

    /** Writes vehicle maintenance records to disk (unlocked). */
    private void writeVehiclesToDiskUnlocked(List<MaintenanceRecord> records) {
        Path path = dataPath(FLEET_FILE_NAME);
        Path parent = path.getParent();
        if (parent != null) {
            try {
                Files.createDirectories(parent);
            } catch (IOException e) {
                throw new StorageException("Failed to create parent directories", e);
            }
        }
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile(parent, path.getFileName().toString(), ".tmp");
            try (BufferedWriter writer = Files.newBufferedWriter(
                    tempFile,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING)) {

                // Write file header
                writer.write("# id|vehicleId|vehicleType|vehicleModel|mileage|photoFileName|lastServiceDate|nextServiceDate");
                writer.newLine();

                // Format and write each record
                for (MaintenanceRecord record : records) {
                    String lineContent = formatVehicleLine(record);
                    writer.write(lineContent);
                    writer.newLine();
                }
            }
            Files.move(tempFile, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException ex) {
                    log.warn("Failed to delete temp file {}", tempFile, ex);
                }
            }
            throw new StorageException("Maintenance fleet file operation failed", e);
        }
    }

    /** Writes maintenance log entries to disk (unlocked). */
    private void writeEntriesToDiskUnlocked(List<MaintenanceEntry> entries) {
        Path path = dataPath(LOG_FILE_NAME);
        Path parent = path.getParent();
        if (parent != null) {
            try {
                Files.createDirectories(parent);
            } catch (IOException e) {
                throw new StorageException("Failed to create parent directories", e);
            }
        }
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile(parent, path.getFileName().toString(), ".tmp");
            try (BufferedWriter writer = Files.newBufferedWriter(
                    tempFile,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING)) {

                // Write file header
                writer.write("# id|vehicleId|reason|maintenanceDate|notes");
                writer.newLine();

                // Format and write each entry
                for (MaintenanceEntry entry : entries) {
                    String lineContent = formatEntryLine(entry);
                    writer.write(lineContent);
                    writer.newLine();
                }
            }
            Files.move(tempFile, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException ex) {
                    log.warn("Failed to delete temp file {}", tempFile, ex);
                }
            }
            throw new StorageException("Maintenance log file operation failed", e);
        }
    }

    /** Parses a line into a MaintenanceRecord object. */
    private MaintenanceRecord parseVehicleLine(String line) {
        // Split by delimiter
        String[] parts = line.split(DELIMITER, -1);

        // Validate column count
        if (parts.length < FLEET_FIELD_COUNT) {
            throw new IllegalArgumentException("Expected " + FLEET_FIELD_COUNT + " fields, got " + parts.length);
        }

        // Clean fields
        String id = nullSafeTrim(parts[0]);
        String vehicleId = nullSafeTrim(parts[1]);
        String vehicleType = nullSafeTrim(parts[2]);
        String vehicleModel = nullSafeTrim(parts[3]);
        String mileage = nullSafeTrim(parts[4]);
        String photoFileName = nullSafeTrim(parts[5]);
        LocalDate lastServiceDate = parseDate(parts[6]);
        LocalDate nextServiceDate = parseDate(parts[7]);

        // Validate identifiers
        if (id.isEmpty() || vehicleId.isEmpty()) {
            throw new IllegalArgumentException("id and vehicleId are required");
        }

        // Return constructed object
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

    /** Parses a line into a MaintenanceEntry object. */
    private MaintenanceEntry parseEntryLine(String line) {
        String[] parts = line.split(DELIMITER, -1);

        // Validate field count
        if (parts.length < LOG_FIELD_COUNT) {
            throw new IllegalArgumentException("Expected " + LOG_FIELD_COUNT + " fields, got " + parts.length);
        }

        // Clean fields
        String id = nullSafeTrim(parts[0]);
        String vehicleId = nullSafeTrim(parts[1]);
        String reason = nullSafeTrim(parts[2]);
        LocalDate maintenanceDate = parseDate(parts[3]);
        String notes = nullSafeTrim(parts[4]);

        // Validate identifiers
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

    /** Parses legacy format lines into a MaintenanceRecord. */
    private MaintenanceRecord parseLegacyVehicleLine(String line) {
        String[] parts = line.split(DELIMITER, -1);

        // Legacy format validation
        if (parts.length < LEGACY_MIN_FIELD_COUNT) {
            throw new IllegalArgumentException("Expected at least " + LEGACY_MIN_FIELD_COUNT + " fields, got " + parts.length);
        }

        // Combined legacy record parsing
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

        // Shorter legacy layout parsing
        boolean hasVehicleType = parts.length >= LEGACY_WITH_TYPE_FIELD_COUNT;
        
        String id = nullSafeTrim(parts[0]);
        String vehicleId = nullSafeTrim(parts[1]);
        String vehicleType = hasVehicleType ? nullSafeTrim(parts[2]) : "";
        String vehicleModel = "";
        String mileage = "";
        String photoFileName = "";
        
        // Determine date offsets
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

    /** Formats a MaintenanceRecord into a pipe-delimited string. */
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

    /** Formats a MaintenanceEntry into a pipe-delimited string. */
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

    /** Parses a string into a LocalDate. */
    private static LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        LocalDate date = LocalDate.parse(value.trim());
        return date;
    }

    /** Formats a LocalDate into a string. */
    private static String formatDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        
        String dateString = date.toString();
        return dateString;
    }

    /** Safely trims a string, returning an empty string if null. */
    private static String nullSafeTrim(String value) {
        if (value == null) {
            return "";
        }
        
        String trimmed = value.trim();
        return trimmed;
    }

    /** Returns empty string if input is null. */
    private static String nullSafe(String value) {
        if (value == null) {
            return "";
        }
        
        return value;
    }

    /** Replaces pipe character with a forward slash to prevent delimiter conflicts. */
    private static String storageSafe(String value) {
        String sanitized = nullSafe(value).replace("|", "/");
        return sanitized;
    }
}
