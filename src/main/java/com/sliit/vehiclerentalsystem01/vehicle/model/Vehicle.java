package com.sliit.vehiclerentalsystem01.vehicle.model;

public abstract class Vehicle {

    private String id;
    private String brand;
    private String model;
    private double dailyRate;

    public Vehicle() {
        this.id = "";
        this.brand = "";
        this.model = "";
        this.dailyRate = 0.0;
    }

    protected Vehicle(String id, String brand, String model, double dailyRate) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.dailyRate = dailyRate;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(double dailyRate) {
        this.dailyRate = dailyRate;
    }

    public abstract String getVehicleType();

    public abstract double calculateRentalRate();

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

    public abstract String getSpecificDetail();

    public abstract String toCSV();
}
