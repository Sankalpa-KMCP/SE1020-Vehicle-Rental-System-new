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

    private Path dataPath(String fileName) {
        return Paths.get(dataDirectory).toAbsolutePath().normalize().resolve(fileName);
    }

    public List<MaintenanceRecord> findAllVehicles() {
        readLock.lock();
        try {
            boolean fleetFileExists = Files.exists(dataPath(FLEET_FILE_NAME));
            List<MaintenanceRecord> vehicles = readVehiclesFromDiskUnlocked();

            if (!fleetFileExists && vehicles.isEmpty()) {
                vehicles = readLegacyVehiclesFromDiskUnlocked();
            }

            return vehicles;
        } finally {
            readLock.unlock();
        }
    }

    public List<MaintenanceEntry> findAllEntries() {
        readLock.lock();
        try {
            return readEntriesFromDiskUnlocked();
        } finally {
            readLock.unlock();
        }
    }

    public void saveVehicle(MaintenanceRecord record) {
        writeLock.lock();
        try {
            List<MaintenanceRecord> records = readVehiclesFromDiskUnlocked();

            records.removeIf(r -> record.getVehicleId().equals(r.getVehicleId()));
            records.add(record);

            writeVehiclesToDiskUnlocked(records);
        } finally {
            writeLock.unlock();
        }
    }

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

    public void deleteVehicleByVehicleId(String vehicleId) {
        if (vehicleId == null || vehicleId.isBlank()) {
            return;
        }

        writeLock.lock();
        try {
            List<MaintenanceRecord> vehicles = readVehiclesFromDiskUnlocked();
            List<MaintenanceEntry> entries = readEntriesFromDiskUnlocked();

            vehicles.removeIf(r -> vehicleId.equals(r.getVehicleId()));
            entries.removeIf(e -> vehicleId.equals(e.getVehicleId()));

            writeVehiclesToDiskUnlocked(vehicles);
            writeEntriesToDiskUnlocked(entries);
        } finally {
            writeLock.unlock();
        }
    }

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

    public void saveAllVehicles(List<MaintenanceRecord> records) {
        writeLock.lock();
        try {
            writeVehiclesToDiskUnlocked(records);
        } finally {
            writeLock.unlock();
        }
    }

    public void saveAllEntries(List<MaintenanceEntry> entries) {
        writeLock.lock();
        try {
            writeEntriesToDiskUnlocked(entries);
        } finally {
            writeLock.unlock();
        }
    }

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
                    vehicles.add(parseVehicleLine(trimmed));
                } catch (RuntimeException ex) {
                    log.warn("Skipping invalid fleet line: {} - {}", trimmed, ex.getMessage());
                }
            }

            return vehicles;
        } catch (IOException e) {
            throw new StorageException("Maintenance fleet file operation failed", e);
        }
    }

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
                    entries.add(parseEntryLine(trimmed));
                } catch (RuntimeException ex) {
                    log.warn("Skipping invalid maintenance entry line: {} - {}", trimmed, ex.getMessage());
                }
            }

            return entries;
        } catch (IOException e) {
            throw new StorageException("Maintenance log file operation failed", e);
        }
    }

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
                    vehicles.add(parseLegacyVehicleLine(trimmed));
                } catch (RuntimeException ex) {
                    log.warn("Skipping invalid legacy maintenance line: {} - {}", trimmed, ex.getMessage());
                }
            }

            return vehicles;
        } catch (IOException e) {
            throw new StorageException("Legacy maintenance file operation failed", e);
        }
    }

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
                    writer.write(formatVehicleLine(record));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new StorageException("Maintenance fleet file operation failed", e);
        }
    }

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
                    writer.write(formatEntryLine(entry));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new StorageException("Maintenance log file operation failed", e);
        }
    }

    private MaintenanceRecord parseVehicleLine(String line) {
        String[] parts = line.split(DELIMITER, -1);

        if (parts.length < FLEET_FIELD_COUNT) {
            throw new IllegalArgumentException("Expected " + FLEET_FIELD_COUNT + " fields, got " + parts.length);
        }

        String id = nullSafeTrim(parts[0]);
        String vehicleId = nullSafeTrim(parts[1]);

        if (id.isEmpty() || vehicleId.isEmpty()) {
            throw new IllegalArgumentException("id and vehicleId are required");
        }

        return new MaintenanceRecord(
                id,
                vehicleId,
                nullSafeTrim(parts[2]),
                nullSafeTrim(parts[3]),
                nullSafeTrim(parts[4]),
                nullSafeTrim(parts[5]),
                parseDate(parts[6]),
                parseDate(parts[7]));
    }

    private MaintenanceEntry parseEntryLine(String line) {
        String[] parts = line.split(DELIMITER, -1);

        if (parts.length < LOG_FIELD_COUNT) {
            throw new IllegalArgumentException("Expected " + LOG_FIELD_COUNT + " fields, got " + parts.length);
        }

        String id = nullSafeTrim(parts[0]);
        String vehicleId = nullSafeTrim(parts[1]);

        if (id.isEmpty() || vehicleId.isEmpty()) {
            throw new IllegalArgumentException("id and vehicleId are required");
        }

        return new MaintenanceEntry(
                id,
                vehicleId,
                nullSafeTrim(parts[2]),
                parseDate(parts[3]),
                nullSafeTrim(parts[4]));
    }

    private MaintenanceRecord parseLegacyVehicleLine(String line) {
        String[] parts = line.split(DELIMITER, -1);

        if (parts.length < LEGACY_MIN_FIELD_COUNT) {
            throw new IllegalArgumentException("Expected at least " + LEGACY_MIN_FIELD_COUNT + " fields, got " + parts.length);
        }

        if (parts.length >= LEGACY_COMBINED_FIELD_COUNT) {
            return new MaintenanceRecord(
                    nullSafeTrim(parts[0]),
                    nullSafeTrim(parts[1]),
                    nullSafeTrim(parts[2]),
                    nullSafeTrim(parts[3]),
                    nullSafeTrim(parts[4]),
                    nullSafeTrim(parts[5]),
                    parseDate(parts[6]),
                    parseDate(parts[7]));
        }

        boolean hasVehicleType = parts.length >= LEGACY_WITH_TYPE_FIELD_COUNT;

        return new MaintenanceRecord(
                nullSafeTrim(parts[0]),
                nullSafeTrim(parts[1]),
                hasVehicleType ? nullSafeTrim(parts[2]) : "",
                "",
                "",
                "",
                parseDate(hasVehicleType ? parts[4] : parts[3]),
                parseDate(hasVehicleType ? parts[5] : parts[4]));
    }

    private String formatVehicleLine(MaintenanceRecord record) {
        return String.join("|",
                storageSafe(record.getId()),
                storageSafe(record.getVehicleId()),
                storageSafe(record.getVehicleType()),
                storageSafe(record.getVehicleModel()),
                storageSafe(record.getMileage()),
                storageSafe(record.getPhotoFileName()),
                formatDate(record.getLastServiceDate()),
                formatDate(record.getNextServiceDate()));
    }

    private String formatEntryLine(MaintenanceEntry entry) {
        return String.join("|",
                storageSafe(entry.getId()),
                storageSafe(entry.getVehicleId()),
                storageSafe(entry.getReason()),
                formatDate(entry.getMaintenanceDate()),
                storageSafe(entry.getNotes()));
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

    private static String storageSafe(String value) {
        return nullSafe(value).replace("|", "/");
    }
}
