package com.sliit.vehiclerentalsystem01.vehicle.entity;

public class Motorcycle extends Vehicle {

    private boolean helmetIncluded;

    public Motorcycle() {
        super();
        this.helmetIncluded = false;
    }

    // NOTICE THE NEW 'String status' AT THE END OF THIS LINE!
    public Motorcycle(String id, String brand, String model, double dailyRate, boolean helmetIncluded, String imagePath, String status) {
        super(id, brand, model, dailyRate, imagePath, status);
        this.helmetIncluded = helmetIncluded;
    }

    // Getters and Setters
    public boolean isHelmetIncluded() {
        return helmetIncluded;
    }

    public void setHelmetIncluded(boolean helmetIncluded) {
        this.helmetIncluded = helmetIncluded;
    }

    @Override
    public String getVehicleType() {
        return "Motorcycle";
    }

    @Override
    public double calculateRentalRate() {
        return getDailyRate() * 0.90;
    }

    @Override
    public String getSpecificDetail() {
        return helmetIncluded ? "Helmet Included" : "No Helmet";
    }

    @Override
    public String toCSV() {
        return "Motorcycle," + getId() + "," + getBrand() + "," + getModel() + "," + getDailyRate() + ","
                + helmetIncluded + "," + getImagePath() + "," + getStatus();
    }
}