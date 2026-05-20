package com.sliit.vehiclerentalsystem01.vehicle.repository;

import com.sliit.vehiclerentalsystem01.core.exceptions.StorageException;
import com.sliit.vehiclerentalsystem01.vehicle.entity.Car;
import com.sliit.vehiclerentalsystem01.vehicle.entity.Motorcycle;
import com.sliit.vehiclerentalsystem01.vehicle.entity.Vehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Repository
public class VehicleRepository {

    private static final String FILE_NAME = "vehicles.txt";
    private static final Logger log = LoggerFactory.getLogger(VehicleRepository.class);

    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();

    @Value("${app.data.directory:data}")
    private String dataDirectory;

    private Path vehiclesPath() {
        return Paths.get(dataDirectory).toAbsolutePath().normalize().resolve(FILE_NAME);
    }

    public List<Vehicle> findAll() {
        readLock.lock();
        try {
            return readVehiclesFromDiskUnlocked();
        } finally {
            readLock.unlock();
        }
    }

    public void saveVehicle(Vehicle vehicle) {
        writeLock.lock();
        try {
            List<Vehicle> vehicles = readVehiclesFromDiskUnlocked();
            vehicles.removeIf(v -> vehicle.getId().equals(v.getId()));
            vehicles.add(vehicle);
            writeVehiclesToDiskUnlocked(vehicles);
        } finally {
            writeLock.unlock();
        }
    }

    public void deleteVehicleById(String id) {
        if (id == null || id.isBlank()) {
            return;
        }
        writeLock.lock();
        try {
            List<Vehicle> vehicles = readVehiclesFromDiskUnlocked();
            vehicles.removeIf(v -> id.equals(v.getId()));
            writeVehiclesToDiskUnlocked(vehicles);
        } finally {
            writeLock.unlock();
        }
    }

    public void saveAll(List<Vehicle> vehicles) {
        writeLock.lock();
        try {
            writeVehiclesToDiskUnlocked(vehicles);
        } finally {
            writeLock.unlock();
        }
    }

    private List<Vehicle> readVehiclesFromDiskUnlocked() {
        Path path = vehiclesPath();
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }
        try {
            List<Vehicle> result = new ArrayList<>();
            for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                try {
                    result.add(parseLine(trimmed));
                } catch (IllegalArgumentException ex) {
                    log.warn("Skipping malformed vehicle line: {}", trimmed, ex);
                }
            }
            return result;
        } catch (IOException e) {
            throw new StorageException("Vehicle repository file operation failed", e);
        }
    }

    private void writeVehiclesToDiskUnlocked(List<Vehicle> vehicles) {
        Path path = vehiclesPath();
        try {
            Files.createDirectories(path.getParent());
            List<String> lines = new ArrayList<>();
            lines.add("# Car,id,brand,model,dailyRate,seatingCapacity,imagePath,status | Motorcycle,id,brand,model,dailyRate,helmetIncluded,imagePath,status");
            lines.addAll(vehicles.stream().map(this::toLine).toList());
            Files.write(path, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new StorageException("Vehicle repository file operation failed", e);
        }
    }

    private Vehicle parseLine(String line) {
        if (line.contains("|")) {
            return parseLegacyPipeLine(line);
        }
        String[] p = line.split(",", -1);
        String type = p[0].trim();

        String imagePath = (p.length > 6 && !p[6].isBlank()) ? p[6].trim() : "/images/default-vehicle.png";

        // Dynamic status parsing logic (extracting the 8th array column safely)
        String status = (p.length > 7 && !p[7].isBlank()) ? p[7].trim() : "AVAILABLE";

        if (type.equalsIgnoreCase("Car")) {
            return new Car(p[1].trim(), p[2].trim(), p[3].trim(), Double.parseDouble(p[4].trim()),
                    Integer.parseInt(p[5].trim()), imagePath, status);
        }
        if (type.equalsIgnoreCase("Motorcycle")) {
            return new Motorcycle(p[1].trim(), p[2].trim(), p[3].trim(), Double.parseDouble(p[4].trim()),
                    Boolean.parseBoolean(p[5].trim()), imagePath, status);
        }
        throw new IllegalArgumentException("Unknown vehicle type: " + type);
    }

    private Vehicle parseLegacyPipeLine(String line) {
        String[] p = line.split("\\|", -1);
        String defaultImagePath = "/images/default-vehicle.png";
        String defaultStatus = "AVAILABLE";

        return switch (p[0]) {
            case "CAR" -> new Car(
                    p[1], p[2], p[3], Double.parseDouble(p[4]),
                    p.length > 6 ? Integer.parseInt(p[6]) : 4, defaultImagePath, defaultStatus);
            case "MOTORCYCLE" -> {
                boolean helmet = p.length >= 8 && Boolean.parseBoolean(p[7]);
                yield new Motorcycle(p[1], p[2], p[3], Double.parseDouble(p[4]), helmet, defaultImagePath, defaultStatus);
            }
            default -> throw new IllegalArgumentException("Unknown vehicle type: " + p[0]);
        };
    }

    private String toLine(Vehicle v) {
        return v.toCSV();
    }
}