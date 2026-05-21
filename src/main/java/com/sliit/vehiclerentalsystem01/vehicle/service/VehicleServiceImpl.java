package com.sliit.vehiclerentalsystem01.vehicle.service;

import com.sliit.vehiclerentalsystem01.reservation.service.ReservationService;
import com.sliit.vehiclerentalsystem01.core.exceptions.ValidationException;
import com.sliit.vehiclerentalsystem01.vehicle.entity.Vehicle;
import com.sliit.vehiclerentalsystem01.vehicle.repository.VehicleRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final ReservationService reservationService;

    public VehicleServiceImpl(VehicleRepository vehicleRepository, @Lazy ReservationService reservationService) {
        this.vehicleRepository = vehicleRepository;
        this.reservationService = reservationService;
    }

    @Override
    public List<Vehicle> findAll() {
        return vehicleRepository.findAll();
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        if (id == null || id.isBlank()) return Optional.empty();
        return vehicleRepository.findAll().stream().filter(v -> id.equals(v.getId())).findFirst();
    }

    @Override
    public void save(Vehicle vehicle) {
        if (vehicle.getDailyRate() <= 0) throw new ValidationException("invalid_daily_rate");

        // DUPLICATE DEFENSE: This protects your fleet inventory integrity.
        boolean isDuplicate = vehicleRepository.findAll().stream()
                .anyMatch(existing ->
                        existing.getBrand().equalsIgnoreCase(vehicle.getBrand()) &&
                                existing.getModel().equalsIgnoreCase(vehicle.getModel()) &&
                                existing.getSpecificDetail().equalsIgnoreCase(vehicle.getSpecificDetail()) &&
                                !existing.getId().equals(vehicle.getId())
                );

        if (isDuplicate) {
            throw new ValidationException("Duplicate Detected: This vehicle specification already exists in the fleet.");
        }

        if (vehicle.getId() == null || vehicle.getId().isBlank()) {
            vehicle.setId(UUID.randomUUID().toString());
        }

        // --- NOTE FOR RESERVATION TEAM ---
        // My module handles Physical Availability.
        // Use vehicle.setStatus("RENTED") only when the keys are handed over.
        // Use vehicle.setStatus("AVAILABLE") when the vehicle is returned.

        vehicleRepository.saveVehicle(vehicle);
    }

    @Override
    public void deleteById(String id) {
        if (id == null || id.isBlank()) return;
        // This maintains the integrity between modules
        if (reservationService.findUnavailableVehicleIds().contains(id.trim())) {
            throw new IllegalStateException("vehicle_in_use");
        }
        vehicleRepository.deleteVehicleById(id);
    }
}