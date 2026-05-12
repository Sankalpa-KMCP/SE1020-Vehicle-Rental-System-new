package com.sliit.vehiclerentalsystem01.vehicle.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Motorcycle extends Vehicle {

    private boolean helmetIncluded;

    public Motorcycle(String id, String brand, String model, double dailyRate, boolean helmetIncluded) {
        super(id, brand, model, dailyRate);
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
                + helmetIncluded;
    }
}
