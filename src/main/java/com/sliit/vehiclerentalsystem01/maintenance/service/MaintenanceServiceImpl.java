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
 * Service implementation for maintenance.
 */
@Service
public class MaintenanceServiceImpl implements MaintenanceService {

    private static final Pattern VEHICLE_ID_PATTERN = Pattern.compile("^MNT-(\\d+)$");
    private static final Pattern ENTRY_ID_PATTERN = Pattern.compile("^MLG-(\\d+)$");
    private static final Set<String> VEHICLE_TYPES = Set.of("Car", "Van", "Bike", "Cab", "SUV");
    private static final Set<String> MAINTENANCE_REASONS = Set.of(
            "Accident",
            "Interior Clean",
            "New Paint",
            "General Service");

    private final MaintenanceRepository maintenanceRepository;

    /** Constructor. */
    public MaintenanceServiceImpl(MaintenanceRepository maintenanceRepository) {
        this.maintenanceRepository = maintenanceRepository;
    }

    /** Seed default vehicles if database is empty. */
    @PostConstruct
    void init() {
        List<MaintenanceRecord> currentVehicles = maintenanceRepository.findAllVehicles();
        boolean vehiclesAreEmpty = currentVehicles.isEmpty();

        if (vehiclesAreEmpty) {
            List<MaintenanceRecord> seedData = seedVehicles();
            maintenanceRepository.saveAllVehicles(seedData);
        }
    }

    /** Find all vehicle records. */
    @Override
    public List<MaintenanceRecord> findAllVehicles() {
        List<MaintenanceRecord> rawList = maintenanceRepository.findAllVehicles();
        List<MaintenanceRecord> copyList = new ArrayList<>(rawList);
        List<MaintenanceRecord> readOnlyList = Collections.unmodifiableList(copyList);
        
        return readOnlyList;
    }

    /** Find all log entries. */
    @Override
    public List<MaintenanceEntry> findAllEntries() {
        List<MaintenanceEntry> rawList = maintenanceRepository.findAllEntries();
        List<MaintenanceEntry> copyList = new ArrayList<>(rawList);
        List<MaintenanceEntry> readOnlyList = Collections.unmodifiableList(copyList);
        
        return readOnlyList;
    }

    /** Get count of vehicles currently in maintenance. */
    @Override
    public int countInMaintenance() {
        List<MaintenanceEntry> allEntries = maintenanceRepository.findAllEntries();
        int activeMaintenanceCount = allEntries.size();
        
        return activeMaintenanceCount;
    }

    /** Find vehicle by ID. */
    @Override
    public Optional<MaintenanceRecord> findByVehicleId(String vehicleId) {
        if (vehicleId == null || vehicleId.isBlank()) {
            return Optional.empty();
        }

        String searchedVehicleId = vehicleId.trim();

        Optional<MaintenanceRecord> match = maintenanceRepository.findAllVehicles()
                .stream()
                .filter(record -> searchedVehicleId.equals(record.getVehicleId()))
                .findFirst();
                
        return match;
    }

    /** Add a vehicle record. */
    @Override
    public void addVehicle(MaintenanceRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("Fleet vehicle must not be null");
        }

        String cleanedVehicleId = requireText(record.getVehicleId(), "vehicle_id_required");

        // Validate unique vehicle ID
        Optional<MaintenanceRecord> existingRecord = findByVehicleId(cleanedVehicleId);
        if (existingRecord.isPresent()) {
            throw new ValidationException("duplicate_vehicle");
        }

        // Validate date order
        LocalDate lastServiceDate = record.getLastServiceDate();
        LocalDate nextServiceDate = record.getNextServiceDate();
        if (lastServiceDate != null && nextServiceDate != null) {
            boolean isChronologyViolated = nextServiceDate.isBefore(lastServiceDate);
            if (isChronologyViolated) {
                throw new ValidationException("invalid_service_dates");
            }
        }

        // Auto-generate ID if missing
        boolean recordIdIsMissing = record.getId() == null || record.getId().isBlank();
        if (recordIdIsMissing) {
            String newRecordId = nextVehicleRecordId();
            record.setId(newRecordId);
        }

        record.setVehicleId(cleanedVehicleId);
        
        String normalizedType = normalizeVehicleType(record.getVehicleType());
        record.setVehicleType(normalizedType);
        
        String validatedModel = requireText(record.getVehicleModel(), "vehicle_model_required");
        record.setVehicleModel(validatedModel);
        
        String validatedMileage = requireText(record.getMileage(), "mileage_required");
        record.setMileage(validatedMileage);
        
        String cleanedPhoto = cleanText(record.getPhotoFileName());
        record.setPhotoFileName(cleanedPhoto);

        maintenanceRepository.saveVehicle(record);
    }

    /** Add a maintenance entry. */
    @Override
    public void addMaintenanceEntry(MaintenanceEntry entry) {
        if (entry == null) {
            throw new IllegalArgumentException("Maintenance entry must not be null");
        }

        String cleanedVehicleId = requireText(entry.getVehicleId(), "vehicle_id_required");

        // Ensure vehicle exists
        Optional<MaintenanceRecord> targetVehicle = findByVehicleId(cleanedVehicleId);
        if (targetVehicle.isEmpty()) {
            throw new ValidationException("vehicle_not_found");
        }

        // Auto-generate ID if missing
        boolean entryIdIsMissing = entry.getId() == null || entry.getId().isBlank();
        if (entryIdIsMissing) {
            String newEntryId = nextEntryId();
            entry.setId(newEntryId);
        }

        entry.setVehicleId(cleanedVehicleId);
        
        String normalizedReason = normalizeMaintenanceReason(entry.getReason());
        entry.setReason(normalizedReason);
        
        // Default to current date if missing
        LocalDate finalDate = entry.getMaintenanceDate() == null ? LocalDate.now() : entry.getMaintenanceDate();
        entry.setMaintenanceDate(finalDate);
        
        String cleanedNotes = cleanText(entry.getNotes());
        entry.setNotes(cleanedNotes);

        maintenanceRepository.saveEntry(entry);
    }

    /** Delete a vehicle. */
    @Override
    public void deleteVehicle(String vehicleId) {
        if (vehicleId == null || vehicleId.isBlank()) {
            return;
        }

        String trimmedId = vehicleId.trim();
        maintenanceRepository.deleteVehicleByVehicleId(trimmedId);
    }

    /** Delete a maintenance entry. */
    @Override
    public void deleteMaintenanceEntry(String entryId) {
        if (entryId == null || entryId.isBlank()) {
            return;
        }

        String trimmedId = entryId.trim();
        maintenanceRepository.deleteEntryById(trimmedId);
    }

    // Default seeding records
    private List<MaintenanceRecord> seedVehicles() {
        LocalDate today = LocalDate.now();
        List<MaintenanceRecord> records = new ArrayList<>();

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

    // Auto-generate vehicle ID
    private String nextVehicleRecordId() {
        List<MaintenanceRecord> allRecords = maintenanceRepository.findAllVehicles();
        String prefix = "MNT";
        String nextId = nextId(allRecords, VEHICLE_ID_PATTERN, prefix);
        
        return nextId;
    }

    // Auto-generate entry ID
    private String nextEntryId() {
        int highestNumber = 0;

        List<MaintenanceEntry> allEntries = maintenanceRepository.findAllEntries();
        for (MaintenanceEntry entry : allEntries) {
            if (entry.getId() == null) {
                continue;
            }

            String entryIdString = entry.getId().trim();
            Matcher matcher = ENTRY_ID_PATTERN.matcher(entryIdString);

            if (matcher.matches()) {
                String digitGroup = matcher.group(1);
                int currentNumber = Integer.parseInt(digitGroup);
                highestNumber = Math.max(highestNumber, currentNumber);
            }
        }

        int nextIdNumber = highestNumber + 1;
        String formattedId = String.format("MLG-%03d", nextIdNumber);
        
        return formattedId;
    }

    // Next ID helper
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

    // Normalize vehicle type
    private static String normalizeVehicleType(String vehicleType) {
        String cleanedVehicleType = requireText(vehicleType, "invalid_vehicle_type");

        String matchedType = VEHICLE_TYPES.stream()
                .filter(type -> type.equalsIgnoreCase(cleanedVehicleType))
                .findFirst()
                .orElseThrow(() -> new ValidationException("invalid_vehicle_type"));
                
        return matchedType;
    }

    // Normalize maintenance reason
    private static String normalizeMaintenanceReason(String maintenanceReason) {
        String cleanedMaintenanceReason = requireText(maintenanceReason, "invalid_maintenance_reason");

        String matchedReason = MAINTENANCE_REASONS.stream()
                .filter(reason -> reason.equalsIgnoreCase(cleanedMaintenanceReason))
                .findFirst()
                .orElseThrow(() -> new ValidationException("invalid_maintenance_reason"));
                
        return matchedReason;
    }

    // Validate not null/blank and return trimmed
    private static String requireText(String value, String errorCode) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(errorCode);
        }

        String trimmed = value.trim();
        return trimmed;
    }

    // Safe trim helper
    private static String cleanText(String value) {
        if (value == null) {
            return "";
        }
        
        String trimmed = value.trim();
        return trimmed;
    }
}
