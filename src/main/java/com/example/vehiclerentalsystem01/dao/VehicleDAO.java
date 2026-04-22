package com.example.vehiclerentalsystem01.dao;

import com.example.vehiclerentalsystem01.models.Car;
import com.example.vehiclerentalsystem01.models.Motorcycle;
import com.example.vehiclerentalsystem01.models.Vehicle;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO {
    //file handling
    // to get the user's home directory when running in different machines.
    // this is needed only for data accessing (only this class) so we put private.
    //static because the file path belongs to this class. so wont recreate for every object
    //Final because the the fle path shouldnt change by any override.
    private static final String FOLDER_PATH = System.getProperty("user.home") + File.separator + "RentalData";
    private static final String FILE_PATH = FOLDER_PATH + File.separator + "vehicles.txt";

    public VehicleDAO() {
        File folder = new File(FOLDER_PATH);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    public void saveVehicle(Vehicle vehicle) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            // Polymorphism: The object formats itself! No casting needed.
            writer.write(vehicle.formatForFile());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Vehicle> getAllVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return vehicles;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                // Checks for our new 6-part format: Type, ID, Brand, Model, Rate, Detail
                if (data.length == 6) {
                    String type = data[0];
                    String id = data[1];
                    String brand = data[2];
                    String model = data[3];
                    double rate = Double.parseDouble(data[4]);

                    // Build the correct object based on the first word in the text file
                    if ("Car".equals(type)) {
                        int capacity = Integer.parseInt(data[5]);
                        vehicles.add(new Car(id, brand, model, rate, capacity));
                    } else if ("Motorcycle".equals(type)) {
                        boolean helmet = Boolean.parseBoolean(data[5]);
                        vehicles.add(new Motorcycle(id, brand, model, rate, helmet));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return vehicles;
    }

    public void deleteVehicle(String id) {
        List<Vehicle> vehicles = getAllVehicles();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Vehicle v : vehicles) {
                if (!v.getId().equals(id)) {
                    // Fixed the typo: using 'v' instead of 'vehicle'
                    writer.write(v.formatForFile());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}