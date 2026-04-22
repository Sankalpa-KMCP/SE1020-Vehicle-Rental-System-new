package com.example.vehiclerentalsystem01.models;
//Vehicle class is common(abstract) to vehicle types we are dealing with.
public class Vehicle {
    // Keeping the variables private so that ins only accessible within this class.( Encapsulation )
    private String id;
    private String brand;
    private String model;
    private double dailyRate;

    // parameterized constructor
    public Vehicle(String id, String brand, String model, double dailyRate) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.dailyRate = dailyRate;
    }

    // Default behaviors for a generic vehicle.
    // Child classes will override these using Polymorphism.

    public String getVehicleType() {
        return "Generic Vehicle";
    }

    public double calculateRentalRate() {
        return dailyRate;
    }

    public String getSpecificDetail() {
        return "Standard";
    }

    //storing object details as "Car,V-8492,Toyota,Camry,50.0,5"
    public String formatForFile() {
        return "Vehicle," + id + "," + brand + "," + model + "," + dailyRate;
    }

    //public getters and setters to access the private attributes.
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public double getDailyRate() { return dailyRate; }
    public void setDailyRate(double dailyRate) { this.dailyRate = dailyRate; }
} //