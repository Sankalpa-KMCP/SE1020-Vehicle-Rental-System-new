package com.example.vehiclerentalsystem01.models;

/**
 * OOP Concept: Abstraction.
 * This is an abstract base class for all vehicles in the fleet.
 * It cannot be instantiated directly.
 */
public abstract class Vehicle {
    // OOP Concept: Encapsulation (Private fields)
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

    /**
     * OOP Concept: Abstraction.
     * Abstract method that forces child classes to define their specific type.
     */
    public abstract String getVehicleType();
    /**
     * OOP Concept: Polymorphism.
     * Forces child classes to define their own rate calculation, specific details, and file formatting.
     */
    public abstract double calculateRentalRate();
    public abstract String getSpecificDetail();
    public abstract String toCSV();

    // Getters and Setters (Encapsulation)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public double getDailyRate() { return dailyRate; }
    public void setDailyRate(double dailyRate) { this.dailyRate = dailyRate; }
}