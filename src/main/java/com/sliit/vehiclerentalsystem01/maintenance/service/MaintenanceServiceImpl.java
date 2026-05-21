package com.sliit.vehiclerentalsystem01.maintenance.service;

import com.sliit.vehiclerentalsystem01.core.exceptions.ValidationException;
import com.sliit.vehiclerentalsystem01.maintenance.entity.MaintenanceEntry;
import com.sliit.vehiclerentalsystem01.maintenance.entity.MaintenanceRecord;
import com.sliit.vehiclerentalsystem01.maintenance.repository.MaintenanceRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of MaintenanceService coordinating business logic,
 * input validation, and data persistence for maintenance.
 */
@Service
public class MaintenanceServiceImpl implements MaintenanceService {

    /** Regex pattern for vehicle record IDs (e.g., MNT-001). */
    private static final Pattern VEHICLE_ID_PATTERN = Pattern.compile("^MNT-(\\d+)$");

    /** Regex pattern for maintenance entry log IDs (e.g., MLG-001). */
    private static final Pattern ENTRY_ID_PATTERN = Pattern.compile("^MLG-(\\d+)$");

    /** Allowed vehicle categories. */
    private static final Set<String> VEHICLE_TYPES = Set.of("Car", "Van", "Bike", "Cab", "SUV");

    /** Allowed reasons for maintenance. */
    private static final Set<String> MAINTENANCE_REASONS = Set.of(
            "Accident",
            "Interior Clean",
            "New Paint",
            "General Service");

    /** Underlying repository dependency. */
    private final MaintenanceRepository maintenanceRepository;

    /** Constructs the service with its repository. */
    public MaintenanceServiceImpl(MaintenanceRepository maintenanceRepository) {
        this.maintenanceRepository = maintenanceRepository;
    }

    /** Seeds default data if storage is empty. */
    @PostConstruct
    void init() {
        // Get existing vehicles
        List<MaintenanceRecord> currentVehicles = maintenanceRepository.findAllVehicles();
        boolean vehiclesAreEmpty = currentVehicles.isEmpty();

        // Seed mock data if empty
        if (vehiclesAreEmpty) {
            List<MaintenanceRecord> seedData = seedVehicles();
            maintenanceRepository.saveAllVehicles(seedData);
        }
    }

    /** Returns a read-only list of all vehicle maintenance records. */
    @Override
    public List<MaintenanceRecord> findAllVehicles() {
        // Return unmodifiable list copy
        List<MaintenanceRecord> rawList = maintenanceRepository.findAllVehicles();
        List<MaintenanceRecord> copyList = new ArrayList<>(rawList);
        List<MaintenanceRecord> readOnlyList = Collections.unmodifiableList(copyList);
        
        return readOnlyList;
    }

    /** Returns a read-only list of all maintenance entries. */
    @Override
    public List<MaintenanceEntry> findAllEntries() {
        List<MaintenanceEntry> rawList = maintenanceRepository.findAllEntries();
        List<MaintenanceEntry> copyList = new ArrayList<>(rawList);
        List<MaintenanceEntry> readOnlyList = Collections.unmodifiableList(copyList);
        
        return readOnlyList;
    }

    /** Returns the count of active maintenance entries. */
    @Override
    public int countInMaintenance() {
        List<MaintenanceEntry> allEntries = maintenanceRepository.findAllEntries();
        int activeMaintenanceCount = allEntries.size();
        
        return activeMaintenanceCount;
    }

    /** Finds a vehicle maintenance record by vehicle ID. */
    @Override
    public Optional<MaintenanceRecord> findByVehicleId(String vehicleId) {
        // Return empty if ID is blank
        if (vehicleId == null || vehicleId.isBlank()) {
            return Optional.empty();
        }

        // Clean ID
        String searchedVehicleId = vehicleId.trim();

        // Filter by vehicle ID
        Optional<MaintenanceRecord> match = maintenanceRepository.findAllVehicles()
                .stream()
                .filter(record -> searchedVehicleId.equals(record.getVehicleId()))
                .findFirst();
                
        return match;
    }

    /** Adds a new vehicle to fleet maintenance with validation. */
    @Override
    public void addVehicle(MaintenanceRecord record) {
        // Validate input
        if (record == null) {
            throw new IllegalArgumentException("Fleet vehicle must not be null");
        }

        // Validate vehicle ID
        String cleanedVehicleId = requireText(record.getVehicleId(), "vehicle_id_required");

        // Check uniqueness
        Optional<MaintenanceRecord> existingRecord = findByVehicleId(cleanedVehicleId);
        if (existingRecord.isPresent()) {
            throw new ValidationException("duplicate_vehicle");
        }

        // Validate service date order
        LocalDate lastServiceDate = record.getLastServiceDate();
        LocalDate nextServiceDate = record.getNextServiceDate();
        if (lastServiceDate != null && nextServiceDate != null) {
            boolean isChronologyViolated = nextServiceDate.isBefore(lastServiceDate);
            if (isChronologyViolated) {
                throw new ValidationException("invalid_service_dates");
            }
        }

        // Generate ID if missing
        boolean recordIdIsMissing = record.getId() == null || record.getId().isBlank();
        if (recordIdIsMissing) {
            String newRecordId = nextVehicleRecordId();
            record.setId(newRecordId);
        }

        // Standardize properties
        record.setVehicleId(cleanedVehicleId);
        
        String normalizedType = normalizeVehicleType(record.getVehicleType());
        record.setVehicleType(normalizedType);
        
        String validatedModel = requireText(record.getVehicleModel(), "vehicle_model_required");
        record.setVehicleModel(validatedModel);
        
        String validatedMileage = requireText(record.getMileage(), "mileage_required");
        record.setMileage(validatedMileage);
        
        String cleanedPhoto = cleanText(record.getPhotoFileName());
        record.setPhotoFileName(cleanedPhoto);

        // Save record
        maintenanceRepository.saveVehicle(record);
    }

    /** Adds a new maintenance log entry for an existing vehicle. */
    @Override
    public void addMaintenanceEntry(MaintenanceEntry entry) {
        // Validate input
        if (entry == null) {
            throw new IllegalArgumentException("Maintenance entry must not be null");
        }

        // Validate vehicle ID
        String cleanedVehicleId = requireText(entry.getVehicleId(), "vehicle_id_required");

        // Check if vehicle exists
        Optional<MaintenanceRecord> targetVehicle = findByVehicleId(cleanedVehicleId);
        if (targetVehicle.isEmpty()) {
            throw new ValidationException("vehicle_not_found");
        }

        // Generate ID if missing
        boolean entryIdIsMissing = entry.getId() == null || entry.getId().isBlank();
        if (entryIdIsMissing) {
            String newEntryId = nextEntryId();
            entry.setId(newEntryId);
        }

        // Standardize properties
        entry.setVehicleId(cleanedVehicleId);
        
        String normalizedReason = normalizeMaintenanceReason(entry.getReason());
        entry.setReason(normalizedReason);
        
        // Default to current date if missing
        LocalDate finalDate = entry.getMaintenanceDate() == null ? LocalDate.now() : entry.getMaintenanceDate();
        entry.setMaintenanceDate(finalDate);
        
        String cleanedNotes = cleanText(entry.getNotes());
        entry.setNotes(cleanedNotes);

        // Save entry
        maintenanceRepository.saveEntry(entry);
    }

    /** Deletes a vehicle by its vehicle ID. */
    @Override
    public void deleteVehicle(String vehicleId) {
        if (vehicleId == null || vehicleId.isBlank()) {
            return;
        }

        String trimmedId = vehicleId.trim();
        maintenanceRepository.deleteVehicleByVehicleId(trimmedId);
    }

    /** Deletes a maintenance entry by its entry ID. */
    @Override
    public void deleteMaintenanceEntry(String entryId) {
        if (entryId == null || entryId.isBlank()) {
            return;
        }

        String trimmedId = entryId.trim();
        maintenanceRepository.deleteEntryById(trimmedId);
    }

    /** Generates default mock records for seeding. */
    private List<MaintenanceRecord> seedVehicles() {
        LocalDate today = LocalDate.now();
        List<MaintenanceRecord> records = new ArrayList<>();

        // Seed Record 1
        records.add(new MaintenanceRecord(
                "MNT-001",
                "VEH-101",
                "Car",
                "Toyota",
                "42500",
                "",
                today.minusMonths(2),
                today.plusMonths(4)
        ));

        // Seed Record 2
        records.add(new MaintenanceRecord(
                "MNT-002",
                "VEH-102",
                "SUV",
                "Nissan",
                "61000",
                "",
                today.minusMonths(1),
                today.plusMonths(5)
        ));

        // Seed Record 3
        records.add(new MaintenanceRecord(
                "MNT-003",
                "VEH-103",
                "Van",
                "Mercedes",
                "78500",
                "",
                today.minusMonths(3),
                today.plusWeeks(2)
        ));

        return records;
    }

    /** Generates the next unique vehicle record ID. */
    private String nextVehicleRecordId() {
        List<MaintenanceRecord> allRecords = maintenanceRepository.findAllVehicles();
        String prefix = "MNT";
        String nextId = nextId(allRecords, VEHICLE_ID_PATTERN, prefix);
        
        return nextId;
    }

    /** Generates the next unique maintenance entry ID. */
    private String nextEntryId() {
        int highestNumber = 0;

        List<MaintenanceEntry> allEntries = maintenanceRepository.findAllEntries();
        for (MaintenanceEntry entry : allEntries) {
            if (entry.getId() == null) {
                continue;
            }

            String entryIdString = entry.getId().trim();
            Matcher matcher = ENTRY_ID_PATTERN.matcher(entryIdString);

            // Parse numerical part if ID matches pattern
            if (matcher.matches()) {
                String digitGroup = matcher.group(1);
                int currentNumber = Integer.parseInt(digitGroup);
                highestNumber = Math.max(highestNumber, currentNumber);
            }
        }

        // Format next ID with leading zeroes
        int nextIdNumber = highestNumber + 1;
        String formattedId = String.format("MLG-%03d", nextIdNumber);
        
        return formattedId;
    }

    /** Helper to generate the next unique ID. */
    private static String nextId(List<MaintenanceRecord> records, Pattern pattern, String prefix) {
        int highestNumber = 0;

        for (MaintenanceRecord record : records) {
            if (record.getId() == null) {
                continue;
            }

            String recordIdString = record.getId().trim();
            Matcher matcher = pattern.matcher(recordIdString);

            if (matcher.matches()) {
                String digitGroup = matcher.group(1);
                int currentNumber = Integer.parseInt(digitGroup);
                highestNumber = Math.max(highestNumber, currentNumber);
            }
        }

        int nextIdNumber = highestNumber + 1;
        String formattedId = String.format("%s-%03d", prefix, nextIdNumber);
        
        return formattedId;
    }

    /** Validates and normalizes vehicle type. */
    private static String normalizeVehicleType(String vehicleType) {
        String cleanedVehicleType = requireText(vehicleType, "invalid_vehicle_type");

        String matchedType = VEHICLE_TYPES.stream()
                .filter(type -> type.equalsIgnoreCase(cleanedVehicleType))
                .findFirst()
                .orElseThrow(() -> new ValidationException("invalid_vehicle_type"));
                
        return matchedType;
    }

    /** Validates and normalizes maintenance reason. */
    private static String normalizeMaintenanceReason(String maintenanceReason) {
        String cleanedMaintenanceReason = requireText(maintenanceReason, "invalid_maintenance_reason");

        String matchedReason = MAINTENANCE_REASONS.stream()
                .filter(reason -> reason.equalsIgnoreCase(cleanedMaintenanceReason))
                .findFirst()
                .orElseThrow(() -> new ValidationException("invalid_maintenance_reason"));
                
        return matchedReason;
    }

    /** Ensures text is not blank and returns trimmed value. */
    private static String requireText(String value, String errorCode) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(errorCode);
        }

        String trimmed = value.trim();
        return trimmed;
    }

    /** Safely trims string, returning empty if null. */
    private static String cleanText(String value) {
        if (value == null) {
            return "";
        }
        
        String trimmed = value.trim();
        return trimmed;
    }
}
