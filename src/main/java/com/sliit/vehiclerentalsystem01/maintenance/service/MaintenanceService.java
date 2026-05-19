package com.sliit.vehiclerentalsystem01.maintenance.service;

import com.sliit.vehiclerentalsystem01.maintenance.entity.MaintenanceRecord;

import java.util.List;
import java.util.Optional;

public interface MaintenanceService {

    List<MaintenanceRecord> findAll();

    int countInMaintenance();

    Optional<MaintenanceRecord> findByVehicleId(String vehicleId);

    void addRecord(MaintenanceRecord record);

    void deleteRecord(String vehicleId);
}
