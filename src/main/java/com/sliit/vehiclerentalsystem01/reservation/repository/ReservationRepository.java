package com.sliit.vehiclerentalsystem01.reservation.repository;

import com.sliit.vehiclerentalsystem01.core.exceptions.StorageException;
import com.sliit.vehiclerentalsystem01.reservation.entity.Booking;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ReservationRepository {

    private static final String FILE_NAME = "bookings.txt";

    @Value("${app.data.directory:data}")
    private String dataDirectory;

    private Path path() {
        return Paths.get(dataDirectory).toAbsolutePath().normalize().resolve(FILE_NAME);
    }

    public List<Booking> findAll() {
        Path p = path();
        if (!Files.exists(p)) {
            return new ArrayList<>();
        }
        try {
            return Files.readAllLines(p, StandardCharsets.UTF_8).stream()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                    .map(this::parseLine)
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            throw new StorageException("Reservation repository file operation failed", e);
        }
    }

    public void saveAll(List<Booking> bookings) {
        Path p = path();
        try {
            Files.createDirectories(p.getParent());
            List<String> lines = new ArrayList<>();
            lines.add("# transactionId,customerName,rentalDate,vehicleId,durationDays,status,staffMessage");
            lines.addAll(bookings.stream().map(this::toLine).toList());
            Files.write(p, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new StorageException("Reservation repository file operation failed", e);
        }
    }

    private Booking parseLine(String line) {
        if (line.contains("|")) {
            return parseLegacyPipeBooking(line);
        }
        String[] a = line.split(",", -1);
        if (a.length < 7) {
            throw new IllegalArgumentException("Invalid booking record: " + line);
        }
        return new Booking(
                a[0].trim(),
                a[1].trim(),
                a[2].trim(),
                a[3].trim(),
                Integer.parseInt(a[4].trim()),
                a[5].trim(),
                a.length > 6 ? a[6].trim() : "");
    }

    private Booking parseLegacyPipeBooking(String line) {
        String[] a = line.split("\\|", -1);
        if (a.length < 7) {
            throw new IllegalArgumentException("Invalid booking record: " + line);
        }
        if ("DAILY".equals(a[0]) || "LONGTERM".equals(a[0])) {
            LocalDate start = LocalDate.parse(a[4]);
            LocalDate end = LocalDate.parse(a[5]);
            int duration = (int) (java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1);
            return new Booking(a[1], a[2], start.toString(), a[3], duration, a[6], "");
        }
        throw new IllegalArgumentException("Unknown booking type: " + a[0]);
    }

    private String toLine(Booking b) {
        return String.join(",",
                esc(b.getTransactionId()),
                esc(b.getCustomerName()),
                esc(b.getRentalDate()),
                esc(b.getVehicleId()),
                Integer.toString(b.getDurationDays()),
                esc(b.getStatus()),
                esc(b.getStaffMessage()));
    }

    private static String esc(String s) {
        if (s == null) {
            return "";
        }
        return s.replace(",", ";");
    }
}
