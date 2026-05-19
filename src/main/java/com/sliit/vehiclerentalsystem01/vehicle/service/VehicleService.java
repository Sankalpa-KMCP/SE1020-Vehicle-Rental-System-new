package com.sliit.vehiclerentalsystem01.vehicle.service;

import com.sliit.vehiclerentalsystem01.vehicle.entity.Vehicle;

import java.util.List;
import java.util.Optional;

public interface VehicleService {

    List<Vehicle> findAll();

    Optional<Vehicle> findById(String id);

    void save(Vehicle vehicle);

    void deleteById(String id);
}
