package com.sliit.vehiclerentalsystem01.maintenance.entity;

import java.time.LocalDate;

public class MaintenanceRecord {

    private String id;
    private String vehicleId;
    private String vehicleType;
    private String vehicleModel;
    private String mileage;
    private String photoFileName;
    private LocalDate lastServiceDate;
    private LocalDate nextServiceDate;
    private String maintenanceReason;
    private String notes;

    public MaintenanceRecord() {
    }

    public MaintenanceRecord(String id,
                             String vehicleId,
                             String vehicleType,
                             String vehicleModel,
                             String mileage,
                             String photoFileName,
                             LocalDate lastServiceDate,
                             LocalDate nextServiceDate,
                             String maintenanceReason,
                             String notes) {
        this.id = id;
        this.vehicleId = vehicleId;
        this.vehicleType = vehicleType;
        this.vehicleModel = vehicleModel;
        this.mileage = mileage;
        this.photoFileName = photoFileName;
        this.lastServiceDate = lastServiceDate;
        this.nextServiceDate = nextServiceDate;
        this.maintenanceReason = maintenanceReason;
        this.notes = notes;
    }

    public MaintenanceRecord(String id,
                             String vehicleId,
                             String vehicleType,
                             LocalDate lastServiceDate,
                             LocalDate nextServiceDate,
                             String notes) {
        this(id, vehicleId, vehicleType, "", "", "", lastServiceDate, nextServiceDate, "", notes);
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

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getMileage() {
        return mileage;
    }

    public void setMileage(String mileage) {
        this.mileage = mileage;
    }

    public String getPhotoFileName() {
        return photoFileName;
    }

    public void setPhotoFileName(String photoFileName) {
        this.photoFileName = photoFileName;
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

    public String getMaintenanceReason() {
        return maintenanceReason;
    }

    public void setMaintenanceReason(String maintenanceReason) {
        this.maintenanceReason = maintenanceReason;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
