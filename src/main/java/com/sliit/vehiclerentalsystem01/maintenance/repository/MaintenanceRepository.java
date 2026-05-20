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
 * MaintenanceRepository is a Spring repository class that manages persistent storage 
 * of maintenance records and logs.
 * <p>
 * Instead of using a traditional relational database, this repository implements 
 * a simple file-based storage mechanism. It persists vehicle maintenance records 
 * and maintenance log entries into delimited text files under a configurable data directory.
 * <p>
 * Thread safety is guaranteed using a {@link ReentrantReadWriteLock}, allowing concurrent 
 * reads but serializing write operations to prevent data corruption during disk updates.
 */
@Repository
public class MaintenanceRepository {

    /**
     * File name for storing the active vehicle fleet maintenance details.
     */
    private static final String FLEET_FILE_NAME = "maintenance-fleet.txt";

    /**
     * File name for storing historical maintenance log entries (history of servicing).
     */
    private static final String LOG_FILE_NAME = "maintenance-log.txt";

    /**
     * File name for legacy maintenance data (used for backwards compatibility migrations).
     */
    private static final String LEGACY_FILE_NAME = "maintenance.txt";

    /**
     * Regular expression delimiter used to split field values within a single line of text files.
     * The fields are delimited by a pipe character ('|').
     */
    private static final String DELIMITER = "\\|";

    /**
     * Minimum number of fields required in legacy format records.
     */
    private static final int LEGACY_MIN_FIELD_COUNT = 6;

    /**
     * Field count for legacy records that explicitly contain a vehicle type field.
     */
    private static final int LEGACY_WITH_TYPE_FIELD_COUNT = 7;

    /**
     * Field count for merged/combined legacy records.
     */
    private static final int LEGACY_COMBINED_FIELD_COUNT = 10;

    /**
     * Standard number of fields expected in the current maintenance-fleet file.
     */
    private static final int FLEET_FIELD_COUNT = 8;

    /**
     * Standard number of fields expected in the current maintenance-log file.
     */
    private static final int LOG_FIELD_COUNT = 5;

    /**
     * Logger utility for recording operational activities, warnings, and errors.
     */
    private static final Logger log = LoggerFactory.getLogger(MaintenanceRepository.class);

    /**
     * A read-write lock to orchestrate concurrent access to file resources.
     */
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    /**
     * Lock used specifically for read operations. Multiple threads can acquire this lock simultaneously
     * as long as no thread has acquired the write lock.
     */
    private final Lock readLock = rwLock.readLock();

    /**
     * Lock used for write operations. Exclusive access is granted. Only one thread can write at a time,
     * and no reads are permitted during write operations.
     */
    private final Lock writeLock = rwLock.writeLock();

    /**
     * Configurable path to the directory where data files are stored.
     * Defaults to the relative directory "data" in the project root if not specified in properties.
     */
    @Value("${app.data.directory:data}")
    private String dataDirectory;

    /**
     * Helper method to construct the absolute normalized file path for a given file name.
     *
     * @param fileName the name of the file
     * @return the resolved Path object
     */
    private Path dataPath(String fileName) {
        Path resolvedPath = Paths.get(dataDirectory)
                                 .toAbsolutePath()
                                 .normalize()
                                 .resolve(fileName);
        return resolvedPath;
    }

    /**
     * Retrieves all vehicle maintenance records from persistence.
     * If the main fleet file does not exist, it falls back to reading and migrating legacy data.
     *
     * @return a List containing all active {@link MaintenanceRecord} entries
     */
    public List<MaintenanceRecord> findAllVehicles() {
        // Acquire the read lock to prevent concurrent writes from mutating the files during read.
        readLock.lock();
        try {
            // Determine if the fleet file currently exists on disk.
            Path fleetFilePath = dataPath(FLEET_FILE_NAME);
            boolean fleetFileExists = Files.exists(fleetFilePath);

            // Read vehicle records from the standard fleet storage file.
            List<MaintenanceRecord> vehicles = readVehiclesFromDiskUnlocked();

            // If the standard fleet file is absent and no vehicles could be parsed,
            // fall back to migrate/load records from the legacy format file.
            if (!fleetFileExists && vehicles.isEmpty()) {
                vehicles = readLegacyVehiclesFromDiskUnlocked();
            }

            return vehicles;
        } finally {
            // Always release the lock in a finally block to avoid deadlocks in case of unexpected exceptions.
            readLock.unlock();
        }
    }

    /**
     * Retrieves all maintenance log entries from persistence.
     *
     * @return a List containing all {@link MaintenanceEntry} records
     */
    public List<MaintenanceEntry> findAllEntries() {
        // Acquire the read lock for thread-safe concurrent access.
        readLock.lock();
        try {
            List<MaintenanceEntry> entries = readEntriesFromDiskUnlocked();
            return entries;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Saves a vehicle maintenance record. If a record with the same vehicle ID already exists,
     * it is updated; otherwise, it is appended as a new record.
     *
     * @param record the {@link MaintenanceRecord} to save
     */
    public void saveVehicle(MaintenanceRecord record) {
        // Acquire the write lock to serialize file write operations.
        writeLock.lock();
        try {
            // Step 1: Read all existing vehicle records.
            List<MaintenanceRecord> records = readVehiclesFromDiskUnlocked();

            // Step 2: Remove any existing record with the matching vehicle ID (updates are performed by replacement).
            String vehicleIdToSave = record.getVehicleId();
            records.removeIf(existingRecord -> vehicleIdToSave.equals(existingRecord.getVehicleId()));

            // Step 3: Add the new/updated record.
            records.add(record);

            // Step 4: Write the updated list back to the physical disk.
            writeVehiclesToDiskUnlocked(records);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Appends a new maintenance entry to the maintenance logs.
     *
     * @param entry the {@link MaintenanceEntry} to save
     */
    public void saveEntry(MaintenanceEntry entry) {
        // Acquire exclusive write lock.
        writeLock.lock();
        try {
            // Step 1: Read the current list of maintenance log entries.
            List<MaintenanceEntry> entries = readEntriesFromDiskUnlocked();

            // Step 2: Append the new entry to the list.
            entries.add(entry);

            // Step 3: Write the updated list back to the physical disk.
            writeEntriesToDiskUnlocked(entries);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Removes a vehicle and all of its associated maintenance entries by vehicle ID.
     *
     * @param vehicleId the unique ID of the vehicle to delete
     */
    public void deleteVehicleByVehicleId(String vehicleId) {
        // Guard check: do nothing if the input ID is invalid or empty.
        if (vehicleId == null || vehicleId.isBlank()) {
            return;
        }

        // Acquire exclusive write lock.
        writeLock.lock();
        try {
            // Step 1: Retrieve existing vehicles and log entries.
            List<MaintenanceRecord> vehicles = readVehiclesFromDiskUnlocked();
            List<MaintenanceEntry> entries = readEntriesFromDiskUnlocked();

            // Step 2: Delete records matching the target vehicle ID.
            vehicles.removeIf(record -> vehicleId.equals(record.getVehicleId()));
            entries.removeIf(entry -> vehicleId.equals(entry.getVehicleId()));

            // Step 3: Persist changes back to disk.
            writeVehiclesToDiskUnlocked(vehicles);
            writeEntriesToDiskUnlocked(entries);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Deletes a single maintenance entry by its unique ID.
     *
     * @param entryId the unique ID of the maintenance entry to remove
     */
    public void deleteEntryById(String entryId) {
        // Guard check: do nothing if the input ID is invalid.
        if (entryId == null || entryId.isBlank()) {
            return;
        }

        // Acquire exclusive write lock.
        writeLock.lock();
        try {
            // Step 1: Retrieve all existing log entries.
            List<MaintenanceEntry> entries = readEntriesFromDiskUnlocked();

            // Step 2: Remove the entry that matches the specified ID.
            entries.removeIf(entry -> entryId.equals(entry.getId()));

            // Step 3: Persist the modified list back to disk.
            writeEntriesToDiskUnlocked(entries);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Overwrites all active vehicle maintenance records on disk.
     *
     * @param records the new list of {@link MaintenanceRecord} objects to persist
     */
    public void saveAllVehicles(List<MaintenanceRecord> records) {
        // Acquire exclusive write lock.
        writeLock.lock();
        try {
            writeVehiclesToDiskUnlocked(records);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Overwrites all maintenance log entries on disk.
     *
     * @param entries the new list of {@link MaintenanceEntry} objects to persist
     */
    public void saveAllEntries(List<MaintenanceEntry> entries) {
        // Acquire exclusive write lock.
        writeLock.lock();
        try {
            writeEntriesToDiskUnlocked(entries);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Internal helper method to read vehicle records from disk. 
     * Assumes the caller has already acquired the appropriate lock.
     *
     * @return a List containing parsed MaintenanceRecord objects
     * @throws StorageException if an filesystem I/O exception occurs
     */
    private List<MaintenanceRecord> readVehiclesFromDiskUnlocked() {
        Path path = dataPath(FLEET_FILE_NAME);

        // If the file does not exist, return an empty mutable list.
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }

        List<MaintenanceRecord> vehicles = new ArrayList<>();

        // Read using a BufferedReader for efficiency.
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;

            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();

                // Skip blank lines and lines starting with '#' which serve as comment headers.
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }

                try {
                    MaintenanceRecord record = parseVehicleLine(trimmed);
                    vehicles.add(record);
                } catch (RuntimeException ex) {
                    // Log warning and skip bad lines, continuing to parse the rest of the file.
                    log.warn("Skipping invalid fleet line: {} - {}", trimmed, ex.getMessage());
                }
            }

            return vehicles;
        } catch (IOException e) {
            throw new StorageException("Maintenance fleet file operation failed", e);
        }
    }

    /**
     * Internal helper method to read maintenance entries from disk. 
     * Assumes the caller has already acquired the appropriate lock.
     *
     * @return a List containing parsed MaintenanceEntry objects
     * @throws StorageException if an filesystem I/O exception occurs
     */
    private List<MaintenanceEntry> readEntriesFromDiskUnlocked() {
        Path path = dataPath(LOG_FILE_NAME);

        // Return a mutable empty list if the log file is not yet initialized.
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }

        List<MaintenanceEntry> entries = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;

            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();

                // Skip blank lines and comment headers.
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

    /**
     * Internal helper method to read and migrate legacy vehicle records from legacy storage.
     * Assumes the caller has already acquired the appropriate lock.
     *
     * @return a List containing parsed legacy MaintenanceRecord objects
     * @throws StorageException if a filesystem I/O exception occurs
     */
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

                // Skip empty lines or header comments.
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

    /**
     * Writes vehicle maintenance records to the primary fleet storage file.
     * Assumes the caller has acquired the write lock.
     *
     * @param records the list of records to persist
     * @throws StorageException if an I/O exception occurs during file creation or writing
     */
    private void writeVehiclesToDiskUnlocked(List<MaintenanceRecord> records) {
        Path path = dataPath(FLEET_FILE_NAME);

        try {
            // Ensure parent directories exist.
            Files.createDirectories(path.getParent());

            // Write to file, truncating existing data.
            try (BufferedWriter writer = Files.newBufferedWriter(
                    path,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING)) {

                // Write descriptive header line.
                writer.write("# id|vehicleId|vehicleType|vehicleModel|mileage|photoFileName|lastServiceDate|nextServiceDate");
                writer.newLine();

                // Format and write each vehicle record.
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

    /**
     * Writes maintenance entry records to the primary log storage file.
     * Assumes the caller has acquired the write lock.
     *
     * @param entries the list of entries to persist
     * @throws StorageException if an I/O exception occurs during file creation or writing
     */
    private void writeEntriesToDiskUnlocked(List<MaintenanceEntry> entries) {
        Path path = dataPath(LOG_FILE_NAME);

        try {
            // Ensure parent directories exist.
            Files.createDirectories(path.getParent());

            // Open buffered writer with create/truncate options.
            try (BufferedWriter writer = Files.newBufferedWriter(
                    path,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING)) {

                // Write metadata header.
                writer.write("# id|vehicleId|reason|maintenanceDate|notes");
                writer.newLine();

                // Format and write each log entry.
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

    /**
     * Parses a single row from the fleet file into a {@link MaintenanceRecord} object.
     *
     * @param line the text line to parse
     * @return the parsed MaintenanceRecord
     * @throws IllegalArgumentException if the line format is invalid
     */
    private MaintenanceRecord parseVehicleLine(String line) {
        // Split line by pipe character. Passing -1 allows trailing empty fields.
        String[] parts = line.split(DELIMITER, -1);

        // Verify that the line contains the required amount of columns.
        if (parts.length < FLEET_FIELD_COUNT) {
            throw new IllegalArgumentException("Expected " + FLEET_FIELD_COUNT + " fields, got " + parts.length);
        }

        // Clean up individual string properties.
        String id = nullSafeTrim(parts[0]);
        String vehicleId = nullSafeTrim(parts[1]);
        String vehicleType = nullSafeTrim(parts[2]);
        String vehicleModel = nullSafeTrim(parts[3]);
        String mileage = nullSafeTrim(parts[4]);
        String photoFileName = nullSafeTrim(parts[5]);
        LocalDate lastServiceDate = parseDate(parts[6]);
        LocalDate nextServiceDate = parseDate(parts[7]);

        // Validate mandatory identifier properties.
        if (id.isEmpty() || vehicleId.isEmpty()) {
            throw new IllegalArgumentException("id and vehicleId are required");
        }

        // Return a constructed record object.
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

    /**
     * Parses a single row from the log file into a {@link MaintenanceEntry} object.
     *
     * @param line the text line to parse
     * @return the parsed MaintenanceEntry
     * @throws IllegalArgumentException if the line format is invalid
     */
    private MaintenanceEntry parseEntryLine(String line) {
        String[] parts = line.split(DELIMITER, -1);

        // Verify minimum field count matches standard requirements.
        if (parts.length < LOG_FIELD_COUNT) {
            throw new IllegalArgumentException("Expected " + LOG_FIELD_COUNT + " fields, got " + parts.length);
        }

        // Clean and prepare values.
        String id = nullSafeTrim(parts[0]);
        String vehicleId = nullSafeTrim(parts[1]);
        String reason = nullSafeTrim(parts[2]);
        LocalDate maintenanceDate = parseDate(parts[3]);
        String notes = nullSafeTrim(parts[4]);

        // Assert mandatory fields.
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

    /**
     * Parses legacy formats of vehicle maintenance lines and constructs a normalized 
     * {@link MaintenanceRecord} object. Handles multiple historical variations of field lengths.
     *
     * @param line the text line to parse
     * @return the normalized MaintenanceRecord
     * @throws IllegalArgumentException if the layout is unrecognized
     */
    private MaintenanceRecord parseLegacyVehicleLine(String line) {
        String[] parts = line.split(DELIMITER, -1);

        // Legacy format must contain at least 6 fields.
        if (parts.length < LEGACY_MIN_FIELD_COUNT) {
            throw new IllegalArgumentException("Expected at least " + LEGACY_MIN_FIELD_COUNT + " fields, got " + parts.length);
        }

        // Format Case 1: Legacy combined record (10+ fields).
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

        // Format Case 2: Shorter legacy layouts. Determine field offsets based on size.
        boolean hasVehicleType = parts.length >= LEGACY_WITH_TYPE_FIELD_COUNT;
        
        String id = nullSafeTrim(parts[0]);
        String vehicleId = nullSafeTrim(parts[1]);
        String vehicleType = hasVehicleType ? nullSafeTrim(parts[2]) : "";
        String vehicleModel = "";
        String mileage = "";
        String photoFileName = "";
        
        // Date positions change depending on presence of the vehicle type field.
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

    /**
     * Formats a {@link MaintenanceRecord} into a pipe-delimited text line suitable for storage.
     *
     * @param record the MaintenanceRecord to format
     * @return the formatted, serialized line string
     */
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

    /**
     * Formats a {@link MaintenanceEntry} into a pipe-delimited text line suitable for storage.
     *
     * @param entry the MaintenanceEntry to format
     * @return the formatted, serialized line string
     */
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

    /**
     * Parses a string representation of a date into a {@link LocalDate} object.
     *
     * @param value the raw date string (expected in ISO format like YYYY-MM-DD)
     * @return the parsed LocalDate, or null if the string is empty or blank
     */
    private static LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        LocalDate date = LocalDate.parse(value.trim());
        return date;
    }

    /**
     * Formats a {@link LocalDate} object into its string representation (YYYY-MM-DD).
     *
     * @param date the LocalDate to format
     * @return the formatted date string, or an empty string if the input is null
     */
    private static String formatDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        
        String dateString = date.toString();
        return dateString;
    }

    /**
     * Helper to safely trim a string. Returns an empty string if the input is null.
     *
     * @param value the string to trim
     * @return the trimmed string, or empty string if input was null
     */
    private static String nullSafeTrim(String value) {
        if (value == null) {
            return "";
        }
        
        String trimmed = value.trim();
        return trimmed;
    }

    /**
     * Helper that returns the original string, or an empty string if the input is null.
     *
     * @param value the string to inspect
     * @return the original string or empty string
     */
    private static String nullSafe(String value) {
        if (value == null) {
            return "";
        }
        
        return value;
    }

    /**
     * Escapes separator characters ('|') inside fields to avoid breaking the file layout.
     * Replaces any pipe character with a forward slash ('/').
     *
     * @param value the raw field value
     * @return the sanitized, safe field string
     */
    private static String storageSafe(String value) {
        String sanitized = nullSafe(value).replace("|", "/");
        return sanitized;
    }
}
