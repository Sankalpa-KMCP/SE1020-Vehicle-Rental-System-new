package com.sliit.vehiclerentalsystem01.vehicle.entity;

public abstract class Vehicle {

    // 1. CLASS VARIABLE (STATIC)
    private static int totalFleetCount = 0;

    // Instance Variables
    private String id;
    private String brand;
    private String model;
    private double dailyRate;
    private String imagePath;
    private String status;

    public Vehicle() {
        this.id = "";
        this.brand = "";
        this.model = "";
        this.dailyRate = 0.0;
        this.imagePath = "";
        this.status = "AVAILABLE";
        totalFleetCount++; 
    }

    protected Vehicle(String id, String brand, String model, double dailyRate, String imagePath, String status) {

        // --- EXCEPTION HANDLING / DATA VALIDATION ---
        if (dailyRate < 0) {
            throw new IllegalArgumentException("Data Security Error: Daily rental rate cannot be a negative number.");
        }
        if (brand == null || brand.trim().isEmpty()) {
            throw new IllegalArgumentException("Data Security Error: Vehicle brand cannot be blank.");
        }
        if (model == null || model.trim().isEmpty()) {
            throw new IllegalArgumentException("Data Security Error: Vehicle model cannot be blank.");
        }


        this.id = id;
        this.brand = brand;
        this.model = model;
        this.dailyRate = dailyRate;
        this.imagePath = imagePath;

        // Logic: Accept only valid fleet states
        this.status = (status != null && (status.equalsIgnoreCase("AVAILABLE") ||
                status.equalsIgnoreCase("MAINTENANCE") ||
                status.equalsIgnoreCase("RENTED")))
                ? status.toUpperCase() : "AVAILABLE";

        totalFleetCount++;
    }

    // Static Getter for the panel
    public static int getTotalFleetCount() {
        return totalFleetCount;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getDailyRate() { return dailyRate; }

    // 2. STANDARD METHOD
    public void setDailyRate(double dailyRate) {
        this.dailyRate = dailyRate;
    }

    // 3. OVERLOADED METHOD (COMPILE-TIME POLYMORPHISM)
    public void setDailyRate(double baseRate, double discountAmount) {
        this.dailyRate = baseRate - discountAmount;
    }

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
    public int hashCode() { return id != null ? id.hashCode() : 0; }
}