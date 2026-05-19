package com.sliit.vehiclerentalsystem01.maintenance.entity;

import java.time.LocalDate;

public class MaintenanceEntry {

    private String id;
    private String vehicleId;
    private String reason;
    private LocalDate maintenanceDate;
    private String notes;

    public MaintenanceEntry() {
    }

    public MaintenanceEntry(String id,
                            String vehicleId,
                            String reason,
                            LocalDate maintenanceDate,
                            String notes) {
        this.id = id;
        this.vehicleId = vehicleId;
        this.reason = reason;
        this.maintenanceDate = maintenanceDate;
        this.notes = notes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDate getMaintenanceDate() {
        return maintenanceDate;
    }

    public void setMaintenanceDate(LocalDate maintenanceDate) {
        this.maintenanceDate = maintenanceDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
