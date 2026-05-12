package com.sliit.vehiclerentalsystem01.payment.service;

import com.sliit.vehiclerentalsystem01.payment.model.CardPayment;
import com.sliit.vehiclerentalsystem01.payment.model.CashPayment;
import com.sliit.vehiclerentalsystem01.payment.model.Payment;
import org.springframework.beans.factory.annotation.Value;
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
public class PaymentFileServiceImpl implements PaymentService {

    private static final String FILE_NAME = "payments.txt";

    @Value("${app.data.directory:data}")
    private String dataDirectory;

    private Path path() {
        return Paths.get(dataDirectory).toAbsolutePath().normalize().resolve(FILE_NAME);
    }

    @Override
    public List<Payment> findAll() {
        return readAll();
    }

    @Override
    public Optional<Payment> findById(String id) {
        return readAll().stream().filter(p -> id.equals(p.getId())).findFirst();
    }

    @Override
    public void save(Payment payment) {
        if (payment.getStatus() == null || payment.getStatus().isBlank()) {
            payment.setStatus("PENDING");
        }
        if (payment.getId() == null || payment.getId().isBlank()) {
            payment.setId(UUID.randomUUID().toString());
        }
        List<Payment> list = readAll();
        list.removeIf(p -> payment.getId().equals(p.getId()));
        list.add(payment);
        writeAll(list);
    }

    @Override
    public void deleteById(String id) {
        List<Payment> list = readAll().stream()
                .filter(p -> !id.equals(p.getId()))
                .collect(Collectors.toCollection(ArrayList::new));
        writeAll(list);
    }

    @Override
    public Optional<Payment> findLatestByBookingId(String bookingId) {
        if (bookingId == null || bookingId.isBlank()) {
            return Optional.empty();
        }
        String key = bookingId.trim();
        Payment latest = null;
        for (Payment p : readAll()) {
            if (key.equals(p.getBookingId())) {
                latest = p;
            }
        }
        return Optional.ofNullable(latest);
    }

    @Override
    public boolean hasAnyPaymentForBooking(String bookingId) {
        if (bookingId == null || bookingId.isBlank()) {
            return false;
        }
        String key = bookingId.trim();
        return readAll().stream().anyMatch(p -> key.equals(p.getBookingId()));
    }

    /**
     * Paid when {@code status} equals {@code PAID} (case-insensitive) or amount is positive with empty/pending treated as unpaid.
     */
    @Override
    public boolean hasPaidPaymentForBooking(String bookingId) {
        if (bookingId == null || bookingId.isBlank()) {
            return false;
        }
        String key = bookingId.trim();
        return readAll().stream()
                .filter(p -> key.equals(p.getBookingId()))
                .anyMatch(p -> "PAID".equalsIgnoreCase(nullSafe(p.getStatus())) || p.getAmount() > 0);
    }

    private static String nullSafe(String s) {
        return s == null ? "" : s.trim();
    }

    private List<Payment> readAll() {
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
            throw new IllegalStateException("Failed to read payments file: " + p, e);
        }
    }

    private Payment parseLine(String line) {
        if (line.contains("|")) {
            return parseLegacyPipePayment(line);
        }
        String[] a = line.split(",", -1);
        if (a.length < 5) {
            throw new IllegalArgumentException("Invalid payment record: " + line);
        }
        String kind = a[0].trim();
        if (kind.equalsIgnoreCase("CARD")) {
            return new CardPayment(a[1].trim(), a[2].trim(), Double.parseDouble(a[3].trim()), a[4].trim());
        }
        if (kind.equalsIgnoreCase("CASH")) {
            return new CashPayment(a[1].trim(), a[2].trim(), Double.parseDouble(a[3].trim()), a[4].trim());
        }
        throw new IllegalArgumentException("Unknown payment type: " + kind);
    }

    private Payment parseLegacyPipePayment(String line) {
        String[] a = line.split("\\|", -1);
        String status = "PAID";
        return switch (a[0]) {
            case "CARD" -> new CardPayment(a[1], a[2], Double.parseDouble(a[3]), status);
            case "CASH" -> new CashPayment(a[1], a[2], Double.parseDouble(a[3]), status);
            default -> throw new IllegalArgumentException("Unknown payment type: " + a[0]);
        };
    }

    private String toLine(Payment p) {
        if (p instanceof CardPayment c) {
            return String.join(",",
                    "CARD",
                    esc(c.getId()),
                    esc(c.getBookingId()),
                    Double.toString(c.getAmount()),
                    esc(c.getStatus()));
        }
        if (p instanceof CashPayment x) {
            return String.join(",",
                    "CASH",
                    esc(x.getId()),
                    esc(x.getBookingId()),
                    Double.toString(x.getAmount()),
                    esc(x.getStatus()));
        }
        throw new IllegalArgumentException("Unsupported payment: " + p.getClass());
    }

    private static String esc(String s) {
        return s == null ? "" : s.replace(",", ";");
    }

    private void writeAll(List<Payment> payments) {
        Path p = path();
        try {
            Files.createDirectories(p.getParent());
            List<String> lines = new ArrayList<>();
            lines.add("# CARD|CASH,id,bookingId,amount,status");
            lines.addAll(payments.stream().map(this::toLine).toList());
            Files.write(p, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write payments file: " + p, e);
        }
    }
}
