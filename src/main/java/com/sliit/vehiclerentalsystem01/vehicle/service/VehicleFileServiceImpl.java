package com.sliit.vehiclerentalsystem01.vehicle.service;

import com.sliit.vehiclerentalsystem01.booking.service.BookingService;
import com.sliit.vehiclerentalsystem01.vehicle.model.Car;
import com.sliit.vehiclerentalsystem01.vehicle.model.Motorcycle;
import com.sliit.vehiclerentalsystem01.vehicle.model.Vehicle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VehicleFileServiceImpl implements VehicleService {

    private static final String FILE_NAME = "vehicles.txt";

    private final BookingService bookingService;

    @Value("${app.data.directory:data}")
    private String dataDirectory;

    /**
     * Booking service is {@link Lazy} because BookingService also depends on UserService and on
     * the bean-resolution path Spring instantiates Vehicle service early.
     */
    public VehicleFileServiceImpl(@Lazy BookingService bookingService) {
        this.bookingService = bookingService;
    }

    private Path vehiclesPath() {
        return Paths.get(dataDirectory).toAbsolutePath().normalize().resolve(FILE_NAME);
    }

    @Override
    public List<Vehicle> findAll() {
        return readAll();
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        return readAll().stream().filter(v -> id.equals(v.getId())).findFirst();
    }

    @Override
    public void save(Vehicle vehicle) {
        if (vehicle.getId() == null || vehicle.getId().isBlank()) {
            vehicle.setId(UUID.randomUUID().toString());
        }
        List<Vehicle> list = readAll();
        list.removeIf(v -> vehicle.getId().equals(v.getId()));
        list.add(vehicle);
        writeAll(list);
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
        if (bookingService.findUnavailableVehicleIds().contains(id.trim())) {
            throw new IllegalStateException("vehicle_in_use");
        }
        List<Vehicle> list = readAll().stream()
                .filter(v -> !id.equals(v.getId()))
                .collect(Collectors.toCollection(ArrayList::new));
        writeAll(list);
    }

    private List<Vehicle> readAll() {
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
            throw new IllegalStateException("Failed to read vehicles file: " + path, e);
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

    private void writeAll(List<Vehicle> vehicles) {
        Path path = vehiclesPath();
        try {
            Files.createDirectories(path.getParent());
            List<String> lines = new ArrayList<>();
            lines.add("# Car,id,brand,model,dailyRate,seatingCapacity | Motorcycle,id,brand,model,dailyRate,helmetIncluded");
            lines.addAll(vehicles.stream().map(this::toLine).toList());
            Files.write(path, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write vehicles file: " + path, e);
        }
    }
}
