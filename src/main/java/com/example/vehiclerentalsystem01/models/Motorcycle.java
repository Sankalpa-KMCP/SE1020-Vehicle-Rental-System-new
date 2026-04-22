package com.example.vehiclerentalsystem01.models;
/**
 * OOP Concept: Inheritance.
 * The Motorcycle class inherits all attributes and methods from the Vehicle class.
 */
public class Motorcycle extends Vehicle {
    private boolean helmetIncluded;

    public Motorcycle(String id, String brand, String model, double dailyRate, boolean helmetIncluded) {
        super(id, brand, model, dailyRate);
        this.helmetIncluded = helmetIncluded;
    }

    public boolean isHelmetIncluded() { return helmetIncluded; }
    public void setHelmetIncluded(boolean helmetIncluded) { this.helmetIncluded = helmetIncluded; }

    // FIX 1: We must satisfy the abstract method from the parent class
    @Override
    public String getVehicleType() {
        return "Motorcycle";
    }

    // --- POLYMORPHISM METHODS ---

    @Override
    public double calculateRentalRate() {
        // FIX 2: We must use getDailyRate() because the variable is private in the parent
        return getDailyRate() * 0.90;
    }

    @Override
    public String getSpecificDetail() {
        return helmetIncluded ? "Helmet Included" : "No Helmet";
    }

    @Override
    public String formatForFile() {
        // FIX 3: We must use Getters to access the parent's private data
        return "Motorcycle," + getId() + "," + getBrand() + "," + getModel() + "," + getDailyRate() + "," + helmetIncluded;
    }
}