package com.sliit.vehiclerentalsystem01.vehicle.entity;

public abstract class Vehicle {

    private String id;
    private String brand;
    private String model;
    private double dailyRate;
    private String imagePath;

    // THE MISSING PIECE:
    private String status;

    public Vehicle() {
        this.id = "";
        this.brand = "";
        this.model = "";
        this.dailyRate = 0.0;
        this.imagePath = "";
        this.status = "AVAILABLE";
    }

    // PARAMETERIZED CONSTRUCTOR:
    protected Vehicle(String id, String brand, String model, double dailyRate, String imagePath, String status) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.dailyRate = dailyRate;
        this.imagePath = imagePath;
        this.status = (status == null || status.isBlank()) ? "AVAILABLE" : status.toUpperCase();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public double getDailyRate() { return dailyRate; }
    public void setDailyRate(double dailyRate) { this.dailyRate = dailyRate; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    // THE MISSING GETTER AND SETTER:
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public abstract String getVehicleType();
    public abstract double calculateRentalRate();
    public abstract String getSpecificDetail();
    public abstract String toCSV();

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vehicle vehicle = (Vehicle) obj;
        return id != null ? id.equals(vehicle.id) : vehicle.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}