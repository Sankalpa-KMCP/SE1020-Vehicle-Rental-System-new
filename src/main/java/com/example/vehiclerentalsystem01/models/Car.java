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
     * Overriding the abstract method from the parent Vehicle class.
     */
    @Override
    public String getVehicleType() {
        return "Car / SUV";
    }
}