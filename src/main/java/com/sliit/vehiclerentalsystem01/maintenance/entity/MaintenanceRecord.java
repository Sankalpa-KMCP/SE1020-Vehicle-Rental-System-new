package com.sliit.vehiclerentalsystem01.maintenance.entity;

import java.time.LocalDate;

public class MaintenanceRecord {

    private String id;
    private String vehicleId;
    private String status;
    private LocalDate lastServiceDate;
    private LocalDate nextServiceDate;
    private String notes;

    public MaintenanceRecord() {
    }

    public MaintenanceRecord(String id,
                             String vehicleId,
                             String status,
                             LocalDate lastServiceDate,
                             LocalDate nextServiceDate,
                             String notes) {
        this.id = id;
        this.vehicleId = vehicleId;
        this.status = status;
        this.lastServiceDate = lastServiceDate;
        this.nextServiceDate = nextServiceDate;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getLastServiceDate() {
        return lastServiceDate;
    }

    public void setLastServiceDate(LocalDate lastServiceDate) {
        this.lastServiceDate = lastServiceDate;
    }

    public LocalDate getNextServiceDate() {
        return nextServiceDate;
    }

    public void setNextServiceDate(LocalDate nextServiceDate) {
        this.nextServiceDate = nextServiceDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}