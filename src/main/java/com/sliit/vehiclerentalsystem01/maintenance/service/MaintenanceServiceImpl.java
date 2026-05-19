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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MaintenanceServiceImpl implements MaintenanceService {

    private static final Pattern ID_PATTERN = Pattern.compile("^MNT-(\\d+)$");

    private final MaintenanceRepository maintenanceRepository;

    public MaintenanceServiceImpl(MaintenanceRepository maintenanceRepository) {
        this.maintenanceRepository = maintenanceRepository;
    }

    @PostConstruct
    void init() {
        if (maintenanceRepository.findAll().isEmpty()) {
            List<MaintenanceRecord> seeded = seedDefaults();
            maintenanceRepository.saveAll(seeded);
        }
    }

    @Override
    public List<MaintenanceRecord> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(maintenanceRepository.findAll()));
    }

    @Override
    public List<MaintenanceRecord> findAvailableVehicles() {
        return maintenanceRepository.findAll().stream()
                .filter(r -> statusMatches(r, MaintenanceStatus.AVAILABLE))
                .toList();
    }

    @Override
    public List<MaintenanceRecord> findInMaintenance() {
        return maintenanceRepository.findAll().stream()
                .filter(r -> statusMatches(r, MaintenanceStatus.MAINTENANCE))
                .toList();
    }

    @Override
    public List<MaintenanceRecord> findInUse() {
        return maintenanceRepository.findAll().stream()
                .filter(r -> statusMatches(r, MaintenanceStatus.IN_USE))
                .toList();
    }

    @Override
    public int countAvailable() {
        return findAvailableVehicles().size();
    }

    @Override
    public int countInMaintenance() {
        return findInMaintenance().size();
    }

    @Override
    public int countInUse() {
        return findInUse().size();
    }

    @Override
    public Optional<MaintenanceRecord> findByVehicleId(String vehicleId) {
        if (vehicleId == null || vehicleId.isBlank()) {
            return Optional.empty();
        }
        String key = vehicleId.trim();
        return maintenanceRepository.findAll().stream()
                .filter(r -> key.equals(r.getVehicleId()))
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
        String vehicleId = record.getVehicleId().trim();
        if (findByVehicleId(vehicleId).isPresent()) {
            throw new IllegalArgumentException("A maintenance record already exists for vehicle: " + vehicleId);
        }
        if (record.getLastServiceDate() != null && record.getNextServiceDate() != null
                && record.getNextServiceDate().isBefore(record.getLastServiceDate())) {
            throw new ValidationException("invalid_service_dates");
        }
        if (record.getId() == null || record.getId().isBlank()) {
            record.setId(nextMaintenanceId());
        }
        if (record.getStatus() != null) {
            validateStatus(record.getStatus());
            record.setStatus(record.getStatus().trim());
        }
        record.setVehicleId(vehicleId);
        maintenanceRepository.saveRecord(record);
    }

    @Override
    public void updateStatus(String vehicleId, String status) {
        if (vehicleId == null || vehicleId.isBlank()) {
            throw new IllegalArgumentException("vehicleId is required");
        }
        validateStatus(status);
        String key = vehicleId.trim();
        MaintenanceRecord record = findByVehicleId(key)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No maintenance record found for vehicle: " + key));
        record.setStatus(status.trim());
        maintenanceRepository.saveRecord(record);
    }

    @Override
    public void deleteRecord(String vehicleId) {
        if (vehicleId == null || vehicleId.isBlank()) {
            return;
        }
        String key = vehicleId.trim();
        maintenanceRepository.deleteRecordByVehicleId(key);
    }

    private List<MaintenanceRecord> seedDefaults() {
        LocalDate today = LocalDate.now();
        List<MaintenanceRecord> records = new ArrayList<>();
        records.add(new MaintenanceRecord(
                "MNT-001",
                "VEH-101",
                MaintenanceStatus.AVAILABLE,
                today.minusMonths(2),
                today.plusMonths(4),
                "Routine inspection completed; ready for rental."));
        records.add(new MaintenanceRecord(
                "MNT-002",
                "VEH-102",
                MaintenanceStatus.IN_USE,
                today.minusMonths(1),
                today.plusMonths(5),
                "Currently on active booking."));
        records.add(new MaintenanceRecord(
                "MNT-003",
                "VEH-103",
                MaintenanceStatus.MAINTENANCE,
                today.minusMonths(3),
                today.minusDays(14),
                "Brake pads replacement in progress."));
        records.add(new MaintenanceRecord(
                "MNT-004",
                "VEH-104",
                MaintenanceStatus.AVAILABLE,
                today.minusWeeks(3),
                today.plusMonths(3),
                "Oil change and tire rotation done."));
        records.add(new MaintenanceRecord(
                "MNT-005",
                "VEH-105",
                MaintenanceStatus.IN_USE,
                today.minusMonths(4),
                today.plusMonths(2),
                "Long-term corporate rental."));
        return records;
    }

    private String nextMaintenanceId() {
        int max = 0;
        for (MaintenanceRecord record : maintenanceRepository.findAll()) {
            if (record.getId() == null) {
                continue;
            }
            Matcher matcher = ID_PATTERN.matcher(record.getId().trim());
            if (matcher.matches()) {
                max = Math.max(max, Integer.parseInt(matcher.group(1)));
            }
        }
        return String.format("MNT-%03d", max + 1);
    }

    private static void validateStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new ValidationException("invalid_status");
        }
        String value = status.trim();
        if (!MaintenanceStatus.AVAILABLE.equals(value)
                && !MaintenanceStatus.IN_USE.equals(value)
                && !MaintenanceStatus.MAINTENANCE.equals(value)) {
            throw new ValidationException("invalid_status");
        }
    }

    private static boolean statusMatches(MaintenanceRecord record, String status) {
        return record != null
                && record.getStatus() != null
                && status.equals(record.getStatus());
    }
}
