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

    public MaintenanceServiceImpl(MaintenanceRepository maintenanceRepository) {
        this.maintenanceRepository = maintenanceRepository;
    }

    @PostConstruct
    void init() {
        boolean vehiclesAreEmpty = maintenanceRepository.findAllVehicles().isEmpty();

        if (vehiclesAreEmpty) {
            maintenanceRepository.saveAllVehicles(seedVehicles());
        }
    }

    @Override
    public List<MaintenanceRecord> findAllVehicles() {
        return Collections.unmodifiableList(new ArrayList<>(maintenanceRepository.findAllVehicles()));
    }

    @Override
    public List<MaintenanceEntry> findAllEntries() {
        return Collections.unmodifiableList(new ArrayList<>(maintenanceRepository.findAllEntries()));
    }

    @Override
    public int countInMaintenance() {
        return maintenanceRepository.findAllEntries().size();
    }

    @Override
    public Optional<MaintenanceRecord> findByVehicleId(String vehicleId) {
        if (vehicleId == null || vehicleId.isBlank()) {
            return Optional.empty();
        }

        String searchedVehicleId = vehicleId.trim();

        return maintenanceRepository.findAllVehicles()
                .stream()
                .filter(record -> searchedVehicleId.equals(record.getVehicleId()))
                .findFirst();
    }

    @Override
    public void addVehicle(MaintenanceRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("Fleet vehicle must not be null");
        }

        String cleanedVehicleId = requireText(record.getVehicleId(), "vehicle_id_required");

        if (findByVehicleId(cleanedVehicleId).isPresent()) {
            throw new ValidationException("duplicate_vehicle");
        }

        LocalDate lastServiceDate = record.getLastServiceDate();
        LocalDate nextServiceDate = record.getNextServiceDate();

        if (lastServiceDate != null && nextServiceDate != null && nextServiceDate.isBefore(lastServiceDate)) {
            throw new ValidationException("invalid_service_dates");
        }

        boolean recordIdIsMissing = record.getId() == null || record.getId().isBlank();

        if (recordIdIsMissing) {
            record.setId(nextVehicleRecordId());
        }

        record.setVehicleId(cleanedVehicleId);
        record.setVehicleType(normalizeVehicleType(record.getVehicleType()));
        record.setVehicleModel(requireText(record.getVehicleModel(), "vehicle_model_required"));
        record.setMileage(requireText(record.getMileage(), "mileage_required"));
        record.setPhotoFileName(cleanText(record.getPhotoFileName()));

        maintenanceRepository.saveVehicle(record);
    }

    @Override
    public void addMaintenanceEntry(MaintenanceEntry entry) {
        if (entry == null) {
            throw new IllegalArgumentException("Maintenance entry must not be null");
        }

        String cleanedVehicleId = requireText(entry.getVehicleId(), "vehicle_id_required");

        if (findByVehicleId(cleanedVehicleId).isEmpty()) {
            throw new ValidationException("vehicle_not_found");
        }

        boolean entryIdIsMissing = entry.getId() == null || entry.getId().isBlank();

        if (entryIdIsMissing) {
            entry.setId(nextEntryId());
        }

        entry.setVehicleId(cleanedVehicleId);
        entry.setReason(normalizeMaintenanceReason(entry.getReason()));
        entry.setMaintenanceDate(entry.getMaintenanceDate() == null ? LocalDate.now() : entry.getMaintenanceDate());
        entry.setNotes(cleanText(entry.getNotes()));

        maintenanceRepository.saveEntry(entry);
    }

    @Override
    public void deleteVehicle(String vehicleId) {
        if (vehicleId == null || vehicleId.isBlank()) {
            return;
        }

        maintenanceRepository.deleteVehicleByVehicleId(vehicleId.trim());
    }

    @Override
    public void deleteMaintenanceEntry(String entryId) {
        if (entryId == null || entryId.isBlank()) {
            return;
        }

        maintenanceRepository.deleteEntryById(entryId.trim());
    }

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
                today.plusMonths(4)));

        records.add(new MaintenanceRecord(
                "MNT-002",
                "VEH-102",
                "SUV",
                "Nissan",
                "61000",
                "",
                today.minusMonths(1),
                today.plusMonths(5)));

        records.add(new MaintenanceRecord(
                "MNT-003",
                "VEH-103",
                "Van",
                "Mercedes",
                "78500",
                "",
                today.minusMonths(3),
                today.plusWeeks(2)));

        return records;
    }

    private String nextVehicleRecordId() {
        return nextId(maintenanceRepository.findAllVehicles(), VEHICLE_ID_PATTERN, "MNT");
    }

    private String nextEntryId() {
        int highestNumber = 0;

        for (MaintenanceEntry entry : maintenanceRepository.findAllEntries()) {
            if (entry.getId() == null) {
                continue;
            }

            Matcher matcher = ENTRY_ID_PATTERN.matcher(entry.getId().trim());

            if (matcher.matches()) {
                highestNumber = Math.max(highestNumber, Integer.parseInt(matcher.group(1)));
            }
        }

        return String.format("MLG-%03d", highestNumber + 1);
    }

    private static String nextId(List<MaintenanceRecord> records, Pattern pattern, String prefix) {
        int highestNumber = 0;

        for (MaintenanceRecord record : records) {
            if (record.getId() == null) {
                continue;
            }

            Matcher matcher = pattern.matcher(record.getId().trim());

            if (matcher.matches()) {
                highestNumber = Math.max(highestNumber, Integer.parseInt(matcher.group(1)));
            }
        }

        return String.format("%s-%03d", prefix, highestNumber + 1);
    }

    private static String normalizeVehicleType(String vehicleType) {
        String cleanedVehicleType = requireText(vehicleType, "invalid_vehicle_type");

        return VEHICLE_TYPES.stream()
                .filter(type -> type.equalsIgnoreCase(cleanedVehicleType))
                .findFirst()
                .orElseThrow(() -> new ValidationException("invalid_vehicle_type"));
    }

    private static String normalizeMaintenanceReason(String maintenanceReason) {
        String cleanedMaintenanceReason = requireText(maintenanceReason, "invalid_maintenance_reason");

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
