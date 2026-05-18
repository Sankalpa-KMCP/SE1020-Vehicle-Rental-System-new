package com.sliit.vehiclerentalsystem01.vehicle.repository;

import com.sliit.vehiclerentalsystem01.core.exceptions.StorageException;
import com.sliit.vehiclerentalsystem01.vehicle.entity.Car;
import com.sliit.vehiclerentalsystem01.vehicle.entity.Motorcycle;
import com.sliit.vehiclerentalsystem01.vehicle.entity.Vehicle;
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
import java.util.stream.Collectors;

@Repository
public class VehicleRepository {

    private static final String FILE_NAME = "vehicles.txt";

    @Value("${app.data.directory:data}")
    private String dataDirectory;

    private Path vehiclesPath() {
        return Paths.get(dataDirectory).toAbsolutePath().normalize().resolve(FILE_NAME);
    }

    public List<Vehicle> findAll() {
        Path path = vehiclesPath();
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }
        try {
            return Files.readAllLines(path, StandardCharsets.UTF_8).stream()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                    .map(this::parseLine)
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            throw new StorageException("Vehicle repository file operation failed", e);
        }
    }

    public void saveAll(List<Vehicle> vehicles) {
        Path path = vehiclesPath();
        try {
            Files.createDirectories(path.getParent());
            List<String> lines = new ArrayList<>();
            lines.add("# Car,id,brand,model,dailyRate,seatingCapacity | Motorcycle,id,brand,model,dailyRate,helmetIncluded");
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
        if (type.equalsIgnoreCase("Car")) {
            return new Car(p[1].trim(), p[2].trim(), p[3].trim(), Double.parseDouble(p[4].trim()),
                    Integer.parseInt(p[5].trim()));
        }
        if (type.equalsIgnoreCase("Motorcycle")) {
            return new Motorcycle(p[1].trim(), p[2].trim(), p[3].trim(), Double.parseDouble(p[4].trim()),
                    Boolean.parseBoolean(p[5].trim()));
        }
        throw new IllegalArgumentException("Unknown vehicle type: " + type);
    }

    private Vehicle parseLegacyPipeLine(String line) {
        String[] p = line.split("\\|", -1);
        return switch (p[0]) {
            case "CAR" -> new Car(
                    p[1], p[2], p[3], Double.parseDouble(p[4]),
                    p.length > 6 ? Integer.parseInt(p[6]) : 4);
            case "MOTORCYCLE" -> {
                boolean helmet = p.length >= 8 && Boolean.parseBoolean(p[7]);
                yield new Motorcycle(p[1], p[2], p[3], Double.parseDouble(p[4]), helmet);
            }
            default -> throw new IllegalArgumentException("Unknown vehicle type: " + p[0]);
        };
    }

    private String toLine(Vehicle v) {
        return v.toCSV();
    }
}
