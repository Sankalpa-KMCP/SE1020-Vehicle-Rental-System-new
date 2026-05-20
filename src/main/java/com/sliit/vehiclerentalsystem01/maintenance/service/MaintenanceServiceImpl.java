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
 * MaintenanceServiceImpl implements the {@link MaintenanceService} interface.
 * It coordinates the business logic, input validation, auto-incrementing ID generation,
 * and data persistence rules for vehicle maintenance records and log entries.
 * <p>
 * Business validation constraints include:
 * <ul>
 *   <li>Preventing duplicate vehicle IDs.</li>
 *   <li>Enforcing that service dates are chronological (next service date must not be before last service date).</li>
 *   <li>Checking that vehicles sent to maintenance actually exist in the database.</li>
 *   <li>Validating case-insensitive vehicle types (Car, Van, etc.) and maintenance reasons.</li>
 * </ul>
 */
@Service
public class MaintenanceServiceImpl implements MaintenanceService {

    /**
     * Regex pattern to match vehicle record IDs. Format: "MNT-" followed by digits (e.g., MNT-001).
     */
    private static final Pattern VEHICLE_ID_PATTERN = Pattern.compile("^MNT-(\\d+)$");

    /**
     * Regex pattern to match maintenance entry log IDs. Format: "MLG-" followed by digits (e.g., MLG-001).
     */
    private static final Pattern ENTRY_ID_PATTERN = Pattern.compile("^MLG-(\\d+)$");

    /**
     * Allowed values for vehicle categories in the system (case-insensitive checks are applied).
     */
    private static final Set<String> VEHICLE_TYPES = Set.of("Car", "Van", "Bike", "Cab", "SUV");

    /**
     * Allowed standard reasons for sending a vehicle into maintenance.
     */
    private static final Set<String> MAINTENANCE_REASONS = Set.of(
            "Accident",
            "Interior Clean",
            "New Paint",
            "General Service");

    /**
     * Reference to the underlying data access repository.
     */
    private final MaintenanceRepository maintenanceRepository;

    /**
     * Constructs a new MaintenanceServiceImpl with its repository dependency.
     *
     * @param maintenanceRepository the repository to handle file operations
     */
    public MaintenanceServiceImpl(MaintenanceRepository maintenanceRepository) {
        this.maintenanceRepository = maintenanceRepository;
    }

    /**
     * Initializes the repository database upon bean creation.
     * If the vehicle fleet storage is empty, it automatically seeds default data.
     */
    @PostConstruct
    void init() {
        // Retrieve the current list of vehicles from the repository.
        List<MaintenanceRecord> currentVehicles = maintenanceRepository.findAllVehicles();
        boolean vehiclesAreEmpty = currentVehicles.isEmpty();

        // Seed with default initial mock records if storage is empty.
        if (vehiclesAreEmpty) {
            List<MaintenanceRecord> seedData = seedVehicles();
            maintenanceRepository.saveAllVehicles(seedData);
        }
    }

    /**
     * Retrieves all active vehicle maintenance records.
     * The list is returned as an unmodifiable view to protect internal state.
     *
     * @return a read-only list of {@link MaintenanceRecord} records
     */
    @Override
    public List<MaintenanceRecord> findAllVehicles() {
        // Create a copy of the list and wrap it to prevent external updates.
        List<MaintenanceRecord> rawList = maintenanceRepository.findAllVehicles();
        List<MaintenanceRecord> copyList = new ArrayList<>(rawList);
        List<MaintenanceRecord> readOnlyList = Collections.unmodifiableList(copyList);
        
        return readOnlyList;
    }

    /**
     * Retrieves all maintenance entry log records.
     * Wrapped in an unmodifiable list for safety.
     *
     * @return a read-only list of {@link MaintenanceEntry} entries
     */
    @Override
    public List<MaintenanceEntry> findAllEntries() {
        List<MaintenanceEntry> rawList = maintenanceRepository.findAllEntries();
        List<MaintenanceEntry> copyList = new ArrayList<>(rawList);
        List<MaintenanceEntry> readOnlyList = Collections.unmodifiableList(copyList);
        
        return readOnlyList;
    }

    /**
     * Calculates the total number of vehicles currently in maintenance.
     *
     * @return the count of active maintenance log entries
     */
    @Override
    public int countInMaintenance() {
        List<MaintenanceEntry> allEntries = maintenanceRepository.findAllEntries();
        int activeMaintenanceCount = allEntries.size();
        
        return activeMaintenanceCount;
    }

    /**
     * Performs a search to locate a maintenance record by vehicle ID.
     *
     * @param vehicleId the vehicle ID to search for
     * @return an {@link Optional} containing the record if found, or empty otherwise
     */
    @Override
    public Optional<MaintenanceRecord> findByVehicleId(String vehicleId) {
        // Return empty Optional immediately if the provided ID is invalid.
        if (vehicleId == null || vehicleId.isBlank()) {
            return Optional.empty();
        }

        // Clean trailing/leading spaces.
        String searchedVehicleId = vehicleId.trim();

        // Stream all vehicles and filter by matching vehicleId.
        Optional<MaintenanceRecord> match = maintenanceRepository.findAllVehicles()
                .stream()
                .filter(record -> searchedVehicleId.equals(record.getVehicleId()))
                .findFirst();
                
        return match;
    }

    /**
     * Adds a new vehicle record to fleet maintenance.
     * Applies validation constraints and automatically generates an ID if missing.
     *
     * @param record the {@link MaintenanceRecord} to save
     * @throws IllegalArgumentException if the record is null
     * @throws ValidationException if business constraints (duplicates, date ranges) are violated
     */
    @Override
    public void addVehicle(MaintenanceRecord record) {
        // Assert input parameter validity.
        if (record == null) {
            throw new IllegalArgumentException("Fleet vehicle must not be null");
        }

        // Validate that vehicleId is provided and clean it.
        String cleanedVehicleId = requireText(record.getVehicleId(), "vehicle_id_required");

        // Validate uniqueness: duplicate vehicleIds are disallowed.
        Optional<MaintenanceRecord> existingRecord = findByVehicleId(cleanedVehicleId);
        if (existingRecord.isPresent()) {
            throw new ValidationException("duplicate_vehicle");
        }

        // Validate service dates chronology.
        LocalDate lastServiceDate = record.getLastServiceDate();
        LocalDate nextServiceDate = record.getNextServiceDate();
        if (lastServiceDate != null && nextServiceDate != null) {
            boolean isChronologyViolated = nextServiceDate.isBefore(lastServiceDate);
            if (isChronologyViolated) {
                throw new ValidationException("invalid_service_dates");
            }
        }

        // Generate and assign an auto-incrementing ID if none is set.
        boolean recordIdIsMissing = record.getId() == null || record.getId().isBlank();
        if (recordIdIsMissing) {
            String newRecordId = nextVehicleRecordId();
            record.setId(newRecordId);
        }

        // Standardize vehicle properties.
        record.setVehicleId(cleanedVehicleId);
        
        String normalizedType = normalizeVehicleType(record.getVehicleType());
        record.setVehicleType(normalizedType);
        
        String validatedModel = requireText(record.getVehicleModel(), "vehicle_model_required");
        record.setVehicleModel(validatedModel);
        
        String validatedMileage = requireText(record.getMileage(), "mileage_required");
        record.setMileage(validatedMileage);
        
        String cleanedPhoto = cleanText(record.getPhotoFileName());
        record.setPhotoFileName(cleanedPhoto);

        // Persist through repository.
        maintenanceRepository.saveVehicle(record);
    }

    /**
     * Adds a new maintenance entry logs for a vehicle.
     * Ensures that the referenced vehicle exists in the system.
     *
     * @param entry the {@link MaintenanceEntry} details to log
     * @throws IllegalArgumentException if the entry is null
     * @throws ValidationException if the vehicle ID does not exist or validation fails
     */
    @Override
    public void addMaintenanceEntry(MaintenanceEntry entry) {
        // Assert input parameter validity.
        if (entry == null) {
            throw new IllegalArgumentException("Maintenance entry must not be null");
        }

        // Ensure the vehicle ID is specified.
        String cleanedVehicleId = requireText(entry.getVehicleId(), "vehicle_id_required");

        // Ensure the vehicle actually exists in the fleet records.
        Optional<MaintenanceRecord> targetVehicle = findByVehicleId(cleanedVehicleId);
        if (targetVehicle.isEmpty()) {
            throw new ValidationException("vehicle_not_found");
        }

        // Generate and assign an auto-incrementing log entry ID if none is set.
        boolean entryIdIsMissing = entry.getId() == null || entry.getId().isBlank();
        if (entryIdIsMissing) {
            String newEntryId = nextEntryId();
            entry.setId(newEntryId);
        }

        // Standardize properties.
        entry.setVehicleId(cleanedVehicleId);
        
        String normalizedReason = normalizeMaintenanceReason(entry.getReason());
        entry.setReason(normalizedReason);
        
        // Default to the current system date if no maintenance date was supplied.
        LocalDate finalDate = entry.getMaintenanceDate() == null ? LocalDate.now() : entry.getMaintenanceDate();
        entry.setMaintenanceDate(finalDate);
        
        String cleanedNotes = cleanText(entry.getNotes());
        entry.setNotes(cleanedNotes);

        // Save through repository.
        maintenanceRepository.saveEntry(entry);
    }

    /**
     * Deletes a vehicle from maintenance records by its vehicle ID.
     *
     * @param vehicleId the vehicle ID to delete
     */
    @Override
    public void deleteVehicle(String vehicleId) {
        if (vehicleId == null || vehicleId.isBlank()) {
            return;
        }

        String trimmedId = vehicleId.trim();
        maintenanceRepository.deleteVehicleByVehicleId(trimmedId);
    }

    /**
     * Deletes a maintenance entry from logs by its entry ID.
     *
     * @param entryId the entry ID to delete
     */
    @Override
    public void deleteMaintenanceEntry(String entryId) {
        if (entryId == null || entryId.isBlank()) {
            return;
        }

        String trimmedId = entryId.trim();
        maintenanceRepository.deleteEntryById(trimmedId);
    }

    /**
     * Generates standard initial mock data records to seed the storage file if it is empty.
     *
     * @return a List of pre-configured {@link MaintenanceRecord} records
     */
    private List<MaintenanceRecord> seedVehicles() {
        LocalDate today = LocalDate.now();
        List<MaintenanceRecord> records = new ArrayList<>();

        // Seeding Record 1
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

        // Seeding Record 2
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

        // Seeding Record 3
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

    /**
     * Computes the next unique vehicle record ID by analyzing existing ones.
     *
     * @return a string ID formatted as "MNT-###"
     */
    private String nextVehicleRecordId() {
        List<MaintenanceRecord> allRecords = maintenanceRepository.findAllVehicles();
        String prefix = "MNT";
        String nextId = nextId(allRecords, VEHICLE_ID_PATTERN, prefix);
        
        return nextId;
    }

    /**
     * Computes the next unique maintenance entry ID by analyzing existing logs.
     * It scans all entries, extracts the numerical component, and increments the highest number.
     *
     * @return a string ID formatted as "MLG-###"
     */
    private String nextEntryId() {
        int highestNumber = 0;

        List<MaintenanceEntry> allEntries = maintenanceRepository.findAllEntries();
        for (MaintenanceEntry entry : allEntries) {
            if (entry.getId() == null) {
                continue;
            }

            String entryIdString = entry.getId().trim();
            Matcher matcher = ENTRY_ID_PATTERN.matcher(entryIdString);

            // If the ID matches our standard pattern, parse the digits group.
            if (matcher.matches()) {
                String digitGroup = matcher.group(1);
                int currentNumber = Integer.parseInt(digitGroup);
                highestNumber = Math.max(highestNumber, currentNumber);
            }
        }

        // Generate incremented code padded with leading zeroes up to 3 digits.
        int nextIdNumber = highestNumber + 1;
        String formattedId = String.format("MLG-%03d", nextIdNumber);
        
        return formattedId;
    }

    /**
     * Generic helper method to parse current entity IDs, find the maximum numeric suffix, 
     * and increment it to return the next valid ID.
     *
     * @param records the list of records to evaluate
     * @param pattern the regular expression to scan IDs
     * @param prefix  the prefix tag (e.g. MNT)
     * @return the computed next ID string
     */
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

    /**
     * Standardizes and validates the vehicle type input against the allowed list of categories.
     * Performs a case-insensitive lookup.
     *
     * @param vehicleType the vehicle type string to validate
     * @return the correctly cased vehicle type matching the allowed set
     * @throws ValidationException if the vehicle type is invalid or missing
     */
    private static String normalizeVehicleType(String vehicleType) {
        String cleanedVehicleType = requireText(vehicleType, "invalid_vehicle_type");

        String matchedType = VEHICLE_TYPES.stream()
                .filter(type -> type.equalsIgnoreCase(cleanedVehicleType))
                .findFirst()
                .orElseThrow(() -> new ValidationException("invalid_vehicle_type"));
                
        return matchedType;
    }

    /**
     * Standardizes and validates the maintenance reason input against the allowed list of reasons.
     *
     * @param maintenanceReason the reason string to validate
     * @return the correctly cased reason matching the allowed set
     * @throws ValidationException if the reason is invalid or missing
     */
    private static String normalizeMaintenanceReason(String maintenanceReason) {
        String cleanedMaintenanceReason = requireText(maintenanceReason, "invalid_maintenance_reason");

        String matchedReason = MAINTENANCE_REASONS.stream()
                .filter(reason -> reason.equalsIgnoreCase(cleanedMaintenanceReason))
                .findFirst()
                .orElseThrow(() -> new ValidationException("invalid_maintenance_reason"));
                
        return matchedReason;
    }

    /**
     * Checks that a required text value is not null or blank, and returns the trimmed value.
     *
     * @param value     the text to inspect
     * @param errorCode the error validation message to throw if validation fails
     * @return the trimmed text
     * @throws ValidationException if value is null or blank
     */
    private static String requireText(String value, String errorCode) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(errorCode);
        }

        String trimmed = value.trim();
        return trimmed;
    }

    /**
     * Helper to safely trim a string. Returns an empty string if null.
     *
     * @param value the string to trim
     * @return the trimmed string, or empty string if input was null
     */
    private static String cleanText(String value) {
        if (value == null) {
            return "";
        }
        
        String trimmed = value.trim();
        return trimmed;
    }
}
