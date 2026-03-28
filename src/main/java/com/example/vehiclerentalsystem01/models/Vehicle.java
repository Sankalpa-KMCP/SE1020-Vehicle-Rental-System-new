package com.example.vehiclerentalsystem01.models;

public abstract class Vehicle {
    private String id;
    private String brand;
    private String model;
    private double dailyRate;

    public Vehicle(String id, String brand, String model, double dailyRate) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.dailyRate = dailyRate;
    }

    public abstract String getVehicleType();

    // --- THESE ARE THE 3 REQUIRED RULES ---
    public abstract double calculateRentalRate();
    public abstract String getSpecificDetail();
    public abstract String toCSV();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public double getDailyRate() { return dailyRate; }
    public void setDailyRate(double dailyRate) { this.dailyRate = dailyRate; }

    //commit
}