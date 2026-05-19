package com.sliit.vehiclerentalsystem01.maintenance.service;

import com.sliit.vehiclerentalsystem01.core.exceptions.ValidationException;
import com.sliit.vehiclerentalsystem01.maintenance.entity.MaintenanceRecord;
import com.sliit.vehiclerentalsystem01.maintenance.entity.MaintenanceStatus;
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

@Service
public class MaintenanceServiceImpl implements MaintenanceService {

    private static final Pattern ID_PATTERN = Pattern.compile("^MNT-(\\d+)$");
    private static final Set<String> VEHICLE_TYPES = Set.of("Car", "Bike", "SUV", "Cab");

    private final MaintenanceRepository maintenanceRepository;

    public MaintenanceServiceImpl(MaintenanceRepository maintenanceRepository) {
        this.maintenanceRepository = maintenanceRepository;
    }

    @PostConstruct
    void init() {
        boolean maintenanceRecordsAreEmpty = maintenanceRepository.findAll().isEmpty();

        if (maintenanceRecordsAreEmpty) {
            List<MaintenanceRecord> defaultMaintenanceRecords = seedDefaults();

            maintenanceRepository.saveAll(defaultMaintenanceRecords);
        }
    }

    @Override
    public List<MaintenanceRecord> findAll() {
        List<MaintenanceRecord> recordsFromRepository = maintenanceRepository.findAll();

        List<MaintenanceRecord> copiedRecords = new ArrayList<>(recordsFromRepository);

        return Collections.unmodifiableList(copiedRecords);
    }

    @Override
    public List<MaintenanceRecord> findAvailableVehicles() {
        return maintenanceRepository.findAll()
                .stream()
                .filter(record -> statusMatches(record, MaintenanceStatus.AVAILABLE))
                .toList();
    }

    @Override
    public List<MaintenanceRecord> findInMaintenance() {
        return maintenanceRepository.findAll()
                .stream()
                .filter(record -> statusMatches(record, MaintenanceStatus.MAINTENANCE))
                .toList();
    }

    @Override
    public List<MaintenanceRecord> findInUse() {
        return maintenanceRepository.findAll()
                .stream()
                .filter(record -> statusMatches(record, MaintenanceStatus.IN_USE))
                .toList();
    }

    @Override
    public int countAvailable() {
        List<MaintenanceRecord> availableVehicles = findAvailableVehicles();

        return availableVehicles.size();
    }

    @Override
    public int countInMaintenance() {
        List<MaintenanceRecord> vehiclesInMaintenance = findInMaintenance();

        return vehiclesInMaintenance.size();
    }

    @Override
    public int countInUse() {
        List<MaintenanceRecord> vehiclesInUse = findInUse();

        return vehiclesInUse.size();
    }

    @Override
    public Optional<MaintenanceRecord> findByVehicleId(String vehicleId) {
        if (vehicleId == null || vehicleId.isBlank()) {
            return Optional.empty();
        }

        String searchedVehicleId = vehicleId.trim();

        return maintenanceRepository.findAll()
                .stream()
                .filter(record -> searchedVehicleId.equals(record.getVehicleId()))
                .findFirst();
    }

    @Override
    public void addRecord(MaintenanceRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("Maintenance record must not be null");
        }

        if (record.getVehicleId() == null || record.getVehicleId().isBlank()) {
            throw new IllegalArgumentException("vehicleId is required");
        }

        String cleanedVehicleId = record.getVehicleId().trim();

        boolean recordAlreadyExists = findByVehicleId(cleanedVehicleId).isPresent();

        if (recordAlreadyExists) {
            throw new IllegalArgumentException("A maintenance record already exists for vehicle: " + cleanedVehicleId);
        }

        LocalDate lastServiceDate = record.getLastServiceDate();
        LocalDate nextServiceDate = record.getNextServiceDate();

        boolean bothServiceDatesExist = lastServiceDate != null && nextServiceDate != null;

        if (bothServiceDatesExist && nextServiceDate.isBefore(lastServiceDate)) {
            throw new ValidationException("invalid_service_dates");
        }

        String cleanedVehicleType = normalizeVehicleType(record.getVehicleType());

        boolean recordIdIsMissing = record.getId() == null || record.getId().isBlank();

        if (recordIdIsMissing) {
            String generatedMaintenanceId = nextMaintenanceId();

            record.setId(generatedMaintenanceId);
        }

        if (record.getStatus() != null) {
            String cleanedStatus = record.getStatus().trim();

            validateStatus(cleanedStatus);

            record.setStatus(cleanedStatus);
        }

        record.setVehicleId(cleanedVehicleId);
        record.setVehicleType(cleanedVehicleType);

        maintenanceRepository.saveRecord(record);
    }

    @Override
    public void updateStatus(String vehicleId, String status) {
        if (vehicleId == null || vehicleId.isBlank()) {
            throw new IllegalArgumentException("vehicleId is required");
        }

        validateStatus(status);

        String cleanedVehicleId = vehicleId.trim();
        String cleanedStatus = status.trim();

        MaintenanceRecord record = findByVehicleId(cleanedVehicleId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No maintenance record found for vehicle: " + cleanedVehicleId));

        record.setStatus(cleanedStatus);

        maintenanceRepository.saveRecord(record);
    }

    @Override
    public void deleteRecord(String vehicleId) {
        if (vehicleId == null || vehicleId.isBlank()) {
            return;
        }

        String cleanedVehicleId = vehicleId.trim();

        maintenanceRepository.deleteRecordByVehicleId(cleanedVehicleId);
    }

    private List<MaintenanceRecord> seedDefaults() {
        LocalDate today = LocalDate.now();

        List<MaintenanceRecord> records = new ArrayList<>();

        MaintenanceRecord firstVehicleRecord = new MaintenanceRecord(
                "MNT-001",
                "VEH-101",
                "Car",
                MaintenanceStatus.AVAILABLE,
                today.minusMonths(2),
                today.plusMonths(4),
                "Routine inspection completed; ready for rental.");

        MaintenanceRecord secondVehicleRecord = new MaintenanceRecord(
                "MNT-002",
                "VEH-102",
                "SUV",
                MaintenanceStatus.IN_USE,
                today.minusMonths(1),
                today.plusMonths(5),
                "Currently on active booking.");

        MaintenanceRecord thirdVehicleRecord = new MaintenanceRecord(
                "MNT-003",
                "VEH-103",
                "Cab",
                MaintenanceStatus.MAINTENANCE,
                today.minusMonths(3),
                today.minusDays(14),
                "Brake pads replacement in progress.");

        MaintenanceRecord fourthVehicleRecord = new MaintenanceRecord(
                "MNT-004",
                "VEH-104",
                "Bike",
                MaintenanceStatus.AVAILABLE,
                today.minusWeeks(3),
                today.plusMonths(3),
                "Oil change and tire rotation done.");

        MaintenanceRecord fifthVehicleRecord = new MaintenanceRecord(
                "MNT-005",
                "VEH-105",
                "Car",
                MaintenanceStatus.IN_USE,
                today.minusMonths(4),
                today.plusMonths(2),
                "Long-term corporate rental.");

        records.add(firstVehicleRecord);
        records.add(secondVehicleRecord);
        records.add(thirdVehicleRecord);
        records.add(fourthVehicleRecord);
        records.add(fifthVehicleRecord);

        return records;
    }

    private String nextMaintenanceId() {
        int highestMaintenanceNumber = 0;

        List<MaintenanceRecord> existingRecords = maintenanceRepository.findAll();

        for (MaintenanceRecord record : existingRecords) {
            if (record.getId() == null) {
                continue;
            }

            String currentRecordId = record.getId().trim();

            Matcher matcher = ID_PATTERN.matcher(currentRecordId);

            boolean idMatchesMaintenancePattern = matcher.matches();

            if (idMatchesMaintenancePattern) {
                String numberPart = matcher.group(1);

                int maintenanceNumber = Integer.parseInt(numberPart);

                highestMaintenanceNumber = Math.max(highestMaintenanceNumber, maintenanceNumber);
            }
        }

        int nextMaintenanceNumber = highestMaintenanceNumber + 1;

        return String.format("MNT-%03d", nextMaintenanceNumber);
    }

    private static void validateStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new ValidationException("invalid_status");
        }

        String cleanedStatus = status.trim();

        boolean statusIsAvailable = MaintenanceStatus.AVAILABLE.equals(cleanedStatus);
        boolean statusIsInUse = MaintenanceStatus.IN_USE.equals(cleanedStatus);
        boolean statusIsMaintenance = MaintenanceStatus.MAINTENANCE.equals(cleanedStatus);

        boolean statusIsValid = statusIsAvailable || statusIsInUse || statusIsMaintenance;

        if (!statusIsValid) {
            throw new ValidationException("invalid_status");
        }
    }

    private static String normalizeVehicleType(String vehicleType) {
        if (vehicleType == null || vehicleType.isBlank()) {
            throw new ValidationException("invalid_vehicle_type");
        }

        String cleanedVehicleType = vehicleType.trim();

        return VEHICLE_TYPES.stream()
                .filter(type -> type.equalsIgnoreCase(cleanedVehicleType))
                .findFirst()
                .orElseThrow(() -> new ValidationException("invalid_vehicle_type"));
    }

    private static boolean statusMatches(MaintenanceRecord record, String status) {
        boolean recordExists = record != null;

        if (!recordExists) {
            return false;
        }

        boolean recordStatusExists = record.getStatus() != null;

        if (!recordStatusExists) {
            return false;
        }

        return status.equals(record.getStatus());
    }
}
