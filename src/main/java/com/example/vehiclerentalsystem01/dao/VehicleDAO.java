package com.example.vehiclerentalsystem01.dao;

import com.example.vehiclerentalsystem01.models.Car;
import com.example.vehiclerentalsystem01.models.Vehicle;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for handling File Read/Write operations for Vehicles.
 */
public class VehicleDAO {
    private static final String FOLDER_PATH = System.getProperty("user.home") + File.separator + "RentalData";
    private static final String FILE_PATH = FOLDER_PATH + File.separator + "vehicles.txt";

    public VehicleDAO() {
        File folder = new File(FOLDER_PATH);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    // CRUD: CREATE
    public void saveVehicle(Vehicle vehicle) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            // We cast to Car to get seating capacity. In a larger system, you'd handle motorcycles/vans too.
            Car car = (Car) vehicle;
            writer.write(car.getId() + "," + car.getBrand() + "," + car.getModel() + "," + car.getDailyRate() + "," + car.getSeatingCapacity());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
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
                String[] data = line.split(",");
                if (data.length == 5) {
                    vehicles.add(new Car(data[0], data[1], data[2], Double.parseDouble(data[3]), Integer.parseInt(data[4])));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return vehicles;
    }

    // CRUD: DELETE
    public void deleteVehicle(String id) {
        List<Vehicle> vehicles = getAllVehicles();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Vehicle v : vehicles) {
                if (!v.getId().equals(id)) {
                    Car car = (Car) v;
                    writer.write(car.getId() + "," + car.getBrand() + "," + car.getModel() + "," + car.getDailyRate() + "," + car.getSeatingCapacity());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }// comment


}