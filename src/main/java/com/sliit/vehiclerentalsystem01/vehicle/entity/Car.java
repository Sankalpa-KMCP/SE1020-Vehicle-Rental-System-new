package com.sliit.vehiclerentalsystem01.vehicle.entity;

// Inheritance
public class Car extends Vehicle {

    private int seatingCapacity;

    public Car() {
        super();
        this.seatingCapacity = 0;
    }

    public Car(String id, String brand, String model, double dailyRate, int seatingCapacity, String imagePath, String status) {
        // Constructor Chaining to the parent
        super(id, brand, model, dailyRate, imagePath, status);

        // Child-specific validation
        if (seatingCapacity <= 0) {
            throw new IllegalArgumentException("Data Security Error: Car seating capacity must be greater than zero.");
        }

        this.seatingCapacity = seatingCapacity;
    }

    public int getSeatingCapacity() { return seatingCapacity; }
    public void setSeatingCapacity(int seatingCapacity) { this.seatingCapacity = seatingCapacity; }

    @Override
    public String getVehicleType() { return "Car / SUV"; }

    @Override
    public double calculateRentalRate() { return getDailyRate(); }

    @Override
    public String getSpecificDetail() { return seatingCapacity + " Seats"; }

    @Override
    public String toCSV() {
        return "Car," + getId() + "," + getBrand() + "," + getModel() + "," + getDailyRate() + "," + seatingCapacity + "," + getImagePath() + "," + getStatus();
    }
}