package com.sliit.vehiclerentalsystem01.booking.service;

import com.sliit.vehiclerentalsystem01.booking.model.Booking;
import com.sliit.vehiclerentalsystem01.payment.service.PaymentService;
import com.sliit.vehiclerentalsystem01.user.model.Customer;
import com.sliit.vehiclerentalsystem01.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingFileServiceImpl implements BookingService {

    private static final String FILE_NAME = "bookings.txt";

    private final UserService userService;
    private final PaymentService paymentService;

    @Value("${app.data.directory:data}")
    private String dataDirectory;

    public BookingFileServiceImpl(UserService userService, @Lazy PaymentService paymentService) {
        this.userService = userService;
        this.paymentService = paymentService;
    }

    private Path path() {
        return Paths.get(dataDirectory).toAbsolutePath().normalize().resolve(FILE_NAME);
    }

    @Override
    public List<Booking> findAll() {
        return readAll();
    }

    @Override
    public List<Booking> findByCustomerId(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            return new ArrayList<>();
        }
        Set<String> keys = customerKeysForBookings(customerId.trim());
        return readAll().stream()
                .filter(b -> keys.contains(b.getCustomerName()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<Booking> findBookingsLinkedToCustomer(Customer customer) {
        if (customer == null) {
            return new ArrayList<>();
        }
        Set<String> keys = new LinkedHashSet<>();
        if (customer.getId() != null && !customer.getId().isBlank()) {
            keys.add(customer.getId().trim());
        }
        if (customer.getUsername() != null && !customer.getUsername().isBlank()) {
            keys.add(customer.getUsername().trim());
        }
        return readAll().stream()
                .filter(b -> keys.contains(b.getCustomerName()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Optional<Booking> findById(String id) {
        return readAll().stream().filter(b -> id.equals(b.getId())).findFirst();
    }

    @Override
    public void save(Booking booking) {
        if (booking.getTransactionId() == null || booking.getTransactionId().isBlank()) {
            booking.setTransactionId(UUID.randomUUID().toString());
        }
        List<Booking> list = readAll();
        list.removeIf(b -> booking.getId().equals(b.getId()));
        list.add(booking);
        writeAll(list);
    }

    /**
     * Refuses to delete a booking that has any associated payment row. Throws
     * {@link IllegalStateException} with code {@code booking_in_use} so controllers can surface
     * a friendly error.
     */
    @Override
    public void deleteById(String id) {
        if (id != null && !id.isBlank() && paymentService.hasAnyPaymentForBooking(id)) {
            throw new IllegalStateException("booking_in_use");
        }
        List<Booking> list = readAll().stream()
                .filter(b -> !id.equals(b.getId()))
                .collect(Collectors.toCollection(ArrayList::new));
        writeAll(list);
    }

    @Override
    public void updateStatus(String bookingId, String newStatus) {
        if (bookingId == null || bookingId.isBlank() || newStatus == null) {
            return;
        }
        List<Booking> list = readAll();
        boolean changed = false;
        for (Booking b : list) {
            if (bookingId.equals(b.getId())) {
                b.setStatus(newStatus);
                changed = true;
                break;
            }
        }
        if (changed) {
            writeAll(list);
        }
    }

    @Override
    public boolean isPaymentAllowed(String bookingId) {
        if (bookingId == null || bookingId.isBlank()) {
            return false;
        }
        return readAll().stream()
                .filter(b -> bookingId.equals(b.getId()))
                .anyMatch(b -> "CONFIRMED".equalsIgnoreCase(normalized(b.getStatus())));
    }

    @Override
    public boolean hasEligibleReviewBooking(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            return false;
        }
        Set<String> keys = customerKeysForBookings(customerId.trim());
        if (keys.isEmpty()) {
            return false;
        }
        return readAll().stream()
                .filter(b -> keys.contains(b.getCustomerName()))
                .anyMatch(b -> {
                    String s = normalized(b.getStatus());
                    return "CONFIRMED".equalsIgnoreCase(s) || "COMPLETED".equalsIgnoreCase(s);
                });
    }

    private Set<String> customerKeysForBookings(String customerRef) {
        Set<String> keys = new LinkedHashSet<>();
        String t = customerRef == null ? "" : customerRef.trim();
        if (t.isEmpty()) {
            return keys;
        }
        keys.add(t);
        userService.findByUsername(t)
                .filter(u -> u instanceof Customer)
                .map(u -> (Customer) u)
                .ifPresent(c -> {
                    keys.add(c.getId());
                    keys.add(c.getUsername());
                });
        userService.findById(t)
                .filter(u -> u instanceof Customer)
                .map(u -> (Customer) u)
                .ifPresent(c -> {
                    keys.add(c.getId());
                    keys.add(c.getUsername());
                });
        return keys;
    }

    @Override
    public Set<String> findUnavailableVehicleIds() {
        Set<String> ids = new HashSet<>();
        for (Booking b : readAll()) {
            String s = normalized(b.getStatus());
            if ("CONFIRMED".equalsIgnoreCase(s)
                    || "CANCELLATION_REQUESTED".equalsIgnoreCase(s)
                    || "UPDATED".equalsIgnoreCase(s)) {
                String vid = normalized(b.getVehicleId());
                if (!vid.isEmpty()) {
                    ids.add(vid);
                }
            }
        }
        return ids;
    }

    @Override
    public boolean isVehicleAvailable(String vehicleId) {
        if (vehicleId == null || vehicleId.isBlank()) {
            return false;
        }
        return !findUnavailableVehicleIds().contains(vehicleId.trim());
    }

    @Override
    public boolean hasDateOverlap(String bookingId, LocalDate start, LocalDate end) {
        if (bookingId == null || bookingId.isBlank() || start == null || end == null) {
            return false;
        }
        Optional<Booking> ob = readAll().stream().filter(b -> bookingId.equals(b.getId())).findFirst();
        if (ob.isEmpty()) {
            return false;
        }
        Booking b = ob.get();
        try {
            LocalDate bStart = LocalDate.parse(normalized(b.getRentalDate()));
            int days = Math.max(1, b.getDurationDays());
            LocalDate bEnd = bStart.plusDays(days - 1L);
            return intervalOverlap(bStart, bEnd, start, end);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean intervalOverlap(LocalDate aStart, LocalDate aEnd, LocalDate bStart, LocalDate bEnd) {
        return !aEnd.isBefore(bStart) && !aStart.isAfter(bEnd);
    }

    private static String normalized(String value) {
        return value == null ? "" : value.trim();
    }

    private List<Booking> readAll() {
        Path p = path();
        if (!Files.exists(p)) {
            return new ArrayList<>();
        }
        try {
            return Files.readAllLines(p, StandardCharsets.UTF_8).stream()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                    .map(this::parseLine)
                    .filter(b -> b != null)
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read bookings file: " + p, e);
        }
    }

    private Booking parseLine(String line) {
        if (line.contains("|")) {
            return parseLegacyPipeBooking(line);
        }
        String[] a = line.split(",", -1);
        if (a.length < 7) {
            return null;
        }
        try {
            return new Booking(
                    a[0].trim(),
                    a[1].trim(),
                    a[2].trim(),
                    a[3].trim(),
                    Integer.parseInt(a[4].trim()),
                    a[5].trim(),
                    a.length > 6 ? a[6].trim() : "");
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Booking parseLegacyPipeBooking(String line) {
        String[] a = line.split("\\|", -1);
        if (a.length < 7) {
            return null;
        }
        try {
            if ("DAILY".equals(a[0]) || "LONGTERM".equals(a[0])) {
                LocalDate start = LocalDate.parse(a[4]);
                LocalDate end = LocalDate.parse(a[5]);
                int duration = (int) (java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1);
                return new Booking(a[1], a[2], start.toString(), a[3], duration, a[6], "");
            }
        } catch (Exception e) {
            return null;
        }
        return null;
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

    private void writeAll(List<Booking> bookings) {
        Path p = path();
        try {
            Files.createDirectories(p.getParent());
            List<String> lines = new ArrayList<>();
            lines.add("# transactionId,customerName,rentalDate,vehicleId,durationDays,status,staffMessage");
            lines.addAll(bookings.stream().map(this::toLine).toList());
            Files.write(p, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write bookings file: " + p, e);
        }
    }
}
