package com.example.vehiclerentalsystem01.models;

/**
 * OOP Concept: Inheritance.
 * The Car class inherits all attributes and methods from the Vehicle class.
 */
public class Car extends Vehicle {
    private int seatingCapacity;

    public Car(String id, String brand, String model, double dailyRate, int seatingCapacity) {
        super(id, brand, model, dailyRate); // Calls parent constructor
        this.seatingCapacity = seatingCapacity;
    }

    public int getSeatingCapacity() { return seatingCapacity; }
    public void setSeatingCapacity(int seatingCapacity) { this.seatingCapacity = seatingCapacity; }

    @Override
    public String getVehicleType() {
        return "Car / SUV";
    }

    // --- POLYMORPHISM METHODS ---

    @Override
    public double calculateRentalRate() {
        // Cars pay the standard daily rate
        return getDailyRate();
    }

    @Override
    public String getSpecificDetail() {
        return seatingCapacity + " Seats";
    }

    @Override
    public String formatForFile() {
        // Formats the data perfectly to write into vehicles.txt
        return "Car," + getId() + "," + getBrand() + "," + getModel() + "," + getDailyRate() + "," + seatingCapacity;
    }
} //