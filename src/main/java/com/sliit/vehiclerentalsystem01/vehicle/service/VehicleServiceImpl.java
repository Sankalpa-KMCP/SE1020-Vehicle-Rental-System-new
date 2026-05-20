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

    /**
     * Reservation service is {@link Lazy} because ReservationService also depends on UserService and on
     * the bean-resolution path Spring instantiates Vehicle service early.
     */
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
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        return vehicleRepository.findAll().stream()
                .filter(v -> id.equals(v.getId()))
                .findFirst();
    }

    @Override
    public void save(Vehicle vehicle) {
        if (vehicle.getDailyRate() <= 0) {
            throw new ValidationException("invalid_daily_rate");
        }
        if (vehicle.getId() == null || vehicle.getId().isBlank()) {
            vehicle.setId(UUID.randomUUID().toString());
        }
        vehicleRepository.saveVehicle(vehicle);
    }

    /**
     * Refuses to delete a vehicle that an active booking still references. Throws
     * {@link IllegalStateException} with code {@code vehicle_in_use} so controllers can surface
     * a friendly error.
     */
    @Override
    public void deleteById(String id) {
        if (id == null || id.isBlank()) {
            return;
        }
        if (reservationService.findUnavailableVehicleIds().contains(id.trim())) {
            throw new IllegalStateException("vehicle_in_use");
        }
        vehicleRepository.deleteVehicleById(id);
    }
}