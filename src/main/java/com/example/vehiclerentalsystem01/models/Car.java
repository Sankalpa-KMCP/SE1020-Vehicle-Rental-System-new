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

    /**
     * OOP Concept: Polymorphism.
     */
    @Override
    public String getVehicleType() {
        return "Car / SUV";
    }

    @Override
    public double calculateRentalRate() {
        return getDailyRate();
    }

    @Override
    public String getSpecificDetail() {
        return seatingCapacity + " Seats";
    }

    @Override
    public String toCSV() {
        // Formats the data perfectly for Student 2 to write into vehicles.txt
        // VITAL: Added "Car," so the DAO knows what this is!
        return "Car," + getId() + "," + getBrand() + "," + getModel() + "," + getDailyRate() + "," + seatingCapacity;
    }

    //comment to commit
}