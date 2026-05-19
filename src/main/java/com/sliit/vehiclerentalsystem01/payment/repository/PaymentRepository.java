package com.sliit.vehiclerentalsystem01.payment.repository;

import com.sliit.vehiclerentalsystem01.core.exceptions.StorageException;
import com.sliit.vehiclerentalsystem01.payment.entity.CardPayment;
import com.sliit.vehiclerentalsystem01.payment.entity.CashPayment;
import com.sliit.vehiclerentalsystem01.payment.entity.Payment;
import com.sliit.vehiclerentalsystem01.payment.entity.PaymentStatus;
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
import java.util.stream.Collectors;

@Repository
public class PaymentRepository {

    private static final String FILE_NAME = "payments.txt";

    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();

    @Value("${app.data.directory:data}")
    private String dataDirectory;

    private Path path() {
        return Paths.get(dataDirectory).toAbsolutePath().normalize().resolve(FILE_NAME);
    }

    public List<Payment> findAll() {
        readLock.lock();
        try {
            Path p = path();
            if (!Files.exists(p)) {
                return new ArrayList<>();
            }
            return Files.readAllLines(p, StandardCharsets.UTF_8).stream()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                    .map(this::parseLine)
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            throw new StorageException("Payment repository file operation failed", e);
        } finally {
            readLock.unlock();
        }
    }

    public void saveAll(List<Payment> payments) {
        writeLock.lock();
        try {
            Path p = path();
            Files.createDirectories(p.getParent());
            List<String> lines = new ArrayList<>();
            lines.add("# CARD|CASH,id,bookingId,amount,status");
            lines.addAll(payments.stream().map(this::toLine).toList());
            Files.write(p, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new StorageException("Payment repository file operation failed", e);
        } finally {
            writeLock.unlock();
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
        String status = PaymentStatus.fromString(a[4].trim()).name();
        if (kind.equalsIgnoreCase("CARD")) {
            return new CardPayment(a[1].trim(), a[2].trim(), Double.parseDouble(a[3].trim()), status);
        }
        if (kind.equalsIgnoreCase("CASH")) {
            return new CashPayment(a[1].trim(), a[2].trim(), Double.parseDouble(a[3].trim()), status);
        }
        throw new IllegalArgumentException("Unknown payment type: " + kind);
    }

    private Payment parseLegacyPipePayment(String line) {
        String[] a = line.split("\\|", -1);
        String status = PaymentStatus.PAID.name();
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
}