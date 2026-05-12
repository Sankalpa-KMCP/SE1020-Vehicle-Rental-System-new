package com.sliit.vehiclerentalsystem01.vehicle.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class Vehicle {

    @EqualsAndHashCode.Include
    private String id;
    private String brand;
    private String model;
    private double dailyRate;

    protected Vehicle(String id, String brand, String model, double dailyRate) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.dailyRate = dailyRate;
    }

    public abstract String getVehicleType();

    public abstract double calculateRentalRate();

    public abstract String getSpecificDetail();

    public abstract String toCSV();
}
