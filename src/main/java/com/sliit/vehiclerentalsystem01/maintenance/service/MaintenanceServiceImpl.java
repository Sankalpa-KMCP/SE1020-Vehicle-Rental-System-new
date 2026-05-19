package com.sliit.vehiclerentalsystem01.maintenance.service;

import com.sliit.vehiclerentalsystem01.core.exceptions.ValidationException;
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

@Service
public class MaintenanceServiceImpl implements MaintenanceService {

    private static final Pattern ID_PATTERN = Pattern.compile("^MNT-(\\d+)$");
    private static final Set<String> VEHICLE_TYPES = Set.of("Car", "Van", "Bike", "Cab", "SUV");
    private static final Set<String> MAINTENANCE_REASONS = Set.of(
            "Accident",
            "Interior Clean",
            "New Paint",
            "General Service");

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
    public int countInMaintenance() {
        return maintenanceRepository.findAll().size();
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
            throw new ValidationException("vehicle_id_required");
        }

        String cleanedVehicleId = record.getVehicleId().trim();

        boolean recordAlreadyExists = findByVehicleId(cleanedVehicleId).isPresent();

        if (recordAlreadyExists) {
            throw new ValidationException("duplicate_vehicle");
        }

        LocalDate lastServiceDate = record.getLastServiceDate();
        LocalDate nextServiceDate = record.getNextServiceDate();

        boolean bothServiceDatesExist = lastServiceDate != null && nextServiceDate != null;

        if (bothServiceDatesExist && nextServiceDate.isBefore(lastServiceDate)) {
            throw new ValidationException("invalid_service_dates");
        }

        String cleanedVehicleType = normalizeVehicleType(record.getVehicleType());
        String cleanedVehicleModel = requireText(record.getVehicleModel(), "vehicle_model_required");
        String cleanedMileage = requireText(record.getMileage(), "mileage_required");
        String cleanedMaintenanceReason = normalizeMaintenanceReason(record.getMaintenanceReason());

        boolean recordIdIsMissing = record.getId() == null || record.getId().isBlank();

        if (recordIdIsMissing) {
            String generatedMaintenanceId = nextMaintenanceId();

            record.setId(generatedMaintenanceId);
        }

        record.setVehicleId(cleanedVehicleId);
        record.setVehicleType(cleanedVehicleType);
        record.setVehicleModel(cleanedVehicleModel);
        record.setMileage(cleanedMileage);
        record.setMaintenanceReason(cleanedMaintenanceReason);
        record.setPhotoFileName(cleanText(record.getPhotoFileName()));
        record.setNotes(cleanText(record.getNotes()));

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

        records.add(new MaintenanceRecord(
                "MNT-001",
                "VEH-101",
                "Car",
                "Toyota",
                "42500",
                "",
                today.minusMonths(2),
                today.plusMonths(4),
                "General Service",
                "Routine inspection completed."));

        records.add(new MaintenanceRecord(
                "MNT-002",
                "VEH-102",
                "SUV",
                "Nissan",
                "61000",
                "",
                today.minusMonths(1),
                today.plusMonths(5),
                "Interior Clean",
                "Deep clean scheduled after long rental."));

        records.add(new MaintenanceRecord(
                "MNT-003",
                "VEH-103",
                "Van",
                "Mercedes",
                "78500",
                "",
                today.minusMonths(3),
                today.plusWeeks(2),
                "Accident",
                "Front bumper and paint inspection required."));

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

    private static String normalizeMaintenanceReason(String maintenanceReason) {
        if (maintenanceReason == null || maintenanceReason.isBlank()) {
            throw new ValidationException("invalid_maintenance_reason");
        }

        String cleanedMaintenanceReason = maintenanceReason.trim();

        return MAINTENANCE_REASONS.stream()
                .filter(reason -> reason.equalsIgnoreCase(cleanedMaintenanceReason))
                .findFirst()
                .orElseThrow(() -> new ValidationException("invalid_maintenance_reason"));
    }

    private static String requireText(String value, String errorCode) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(errorCode);
        }

        return value.trim();
    }

    private static String cleanText(String value) {
        return value == null ? "" : value.trim();
    }
}
