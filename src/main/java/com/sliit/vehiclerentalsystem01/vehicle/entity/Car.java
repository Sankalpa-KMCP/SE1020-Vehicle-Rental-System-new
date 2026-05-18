package com.sliit.vehiclerentalsystem01.vehicle.entity;

public class Car extends Vehicle {

    private int seatingCapacity;

    public Car() {
        super();
        this.seatingCapacity = 0;
    }

    public Car(String id, String brand, String model, double dailyRate, int seatingCapacity) {
        super(id, brand, model, dailyRate);
        this.seatingCapacity = seatingCapacity;
    }

    // Getters and Setters
    public int getSeatingCapacity() {
        return seatingCapacity;
    }

    public void setSeatingCapacity(int seatingCapacity) {
        this.seatingCapacity = seatingCapacity;
    }

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
        return "Car," + getId() + "," + getBrand() + "," + getModel() + "," + getDailyRate() + "," + seatingCapacity;
    }
}
