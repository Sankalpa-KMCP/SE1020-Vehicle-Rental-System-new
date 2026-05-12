package com.sliit.vehiclerentalsystem01.vehicle.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Car extends Vehicle {

    private int seatingCapacity;

    public Car(String id, String brand, String model, double dailyRate, int seatingCapacity) {
        super(id, brand, model, dailyRate);
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
