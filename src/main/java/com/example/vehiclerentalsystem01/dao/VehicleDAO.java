package com.example.vehiclerentalsystem01.dao;

import com.example.vehiclerentalsystem01.models.Car;
import com.example.vehiclerentalsystem01.models.Motorcycle;
import com.example.vehiclerentalsystem01.models.Vehicle;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for handling File Read/Write operations for Vehicles.
 */
@Repository
public class VehicleDAO {
    private static final String FOLDER_PATH = System.getProperty("user.home") + File.separator + "RentalData";
    private static final String FILE_PATH = FOLDER_PATH + File.separator + "vehicles.txt";

    public VehicleDAO() {
        File folder = new File(FOLDER_PATH);
        if (!folder.exists() && !folder.mkdirs()) {
            System.err.println("Could not create data folder: " + folder.getAbsolutePath());
        }
    }

    // CRUD: CREATE
    public void saveVehicle(Vehicle vehicle) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(vehicle.toCSV());
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Failed to save vehicle: " + e.getMessage());
        }
    }

    // CRUD: READ
    public List<Vehicle> getAllVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return vehicles;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Vehicle vehicle = parseVehicle(line);
                if (vehicle != null) {
                    vehicles.add(vehicle);
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to read vehicles: " + e.getMessage());
        }
        return vehicles;
    }

    public Vehicle getVehicleById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }

        for (Vehicle vehicle : getAllVehicles()) {
            if (id.equals(vehicle.getId())) {
                return vehicle;
            }
        }
        return null;
    }

    // CRUD: UPDATE
    public void updateVehicle(Vehicle updatedVehicle) {
        List<Vehicle> vehicles = getAllVehicles();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Vehicle vehicle : vehicles) {
                if (vehicle.getId().equals(updatedVehicle.getId())) {
                    writer.write(updatedVehicle.toCSV());
                } else {
                    writer.write(vehicle.toCSV());
                }
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Failed to update vehicle: " + e.getMessage());
        }
    }

    // CRUD: DELETE
    public void deleteVehicle(String id) {
        List<Vehicle> vehicles = getAllVehicles();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Vehicle vehicle : vehicles) {
                if (!vehicle.getId().equals(id)) {
                    writer.write(vehicle.toCSV());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to delete vehicle: " + e.getMessage());
        }
    }

    private Vehicle parseVehicle(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }

        String[] data = line.split(",", -1);
        try {
            if (data.length == 5) {
                // Backward compatibility with the older car-only format.
                return new Car(data[0], data[1], data[2], Double.parseDouble(data[3]), Integer.parseInt(data[4]));
            }

            if (data.length >= 6) {
                String type = data[0].trim();
                if ("Motorcycle".equalsIgnoreCase(type)) {
                    return new Motorcycle(data[1], data[2], data[3], Double.parseDouble(data[4]), Boolean.parseBoolean(data[5]));
                }

                return new Car(data[1], data[2], data[3], Double.parseDouble(data[4]), Integer.parseInt(data[5]));
            }
        } catch (NumberFormatException ex) {
            System.err.println("Failed to parse vehicle record: " + ex.getMessage());
        }

        return null;
    }
}

