package com.sliit.vehiclerentalsystem01.maintenance.service;

import com.sliit.vehiclerentalsystem01.maintenance.entity.MaintenanceEntry;
import com.sliit.vehiclerentalsystem01.maintenance.entity.MaintenanceRecord;

import java.util.List;
import java.util.Optional;

public interface MaintenanceService {

    List<MaintenanceRecord> findAllVehicles();

    List<MaintenanceEntry> findAllEntries();

    int countInMaintenance();

    Optional<MaintenanceRecord> findByVehicleId(String vehicleId);

    void addVehicle(MaintenanceRecord record);

    void addMaintenanceEntry(MaintenanceEntry entry);

    void deleteVehicle(String vehicleId);

    void deleteMaintenanceEntry(String entryId);
}
