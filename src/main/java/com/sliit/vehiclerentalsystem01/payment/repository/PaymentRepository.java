package com.sliit.vehiclerentalsystem01.payment.repository;

import com.sliit.vehiclerentalsystem01.core.exceptions.StorageException;
import com.sliit.vehiclerentalsystem01.payment.entity.CardPayment;
import com.sliit.vehiclerentalsystem01.payment.entity.CashPayment;
import com.sliit.vehiclerentalsystem01.payment.entity.Payment;
import com.sliit.vehiclerentalsystem01.payment.entity.PaymentStatus;
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
public class PaymentRepository {

    private static final String FILE_NAME = "payments.txt";
    private static final Logger log = LoggerFactory.getLogger(PaymentRepository.class);

    // Concurrency control to allow multiple thread reads but exclusive writes
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();

    @Value("${app.data.directory:data}")
    private String dataDirectory;

    /**
     * Resolves the absolute path to the data file.
     */
    private Path path() {
        return Paths.get(dataDirectory).toAbsolutePath().normalize().resolve(FILE_NAME);
    }

    /**
     * CRUD: READ
     * Retrieves all payments from the text file.
     * Uses a read-lock to ensure data consistency during the read operation.
     */
    public List<Payment> findAll() {
        readLock.lock();
        try {
            Path p = path();
            if (!Files.exists(p)) {
                return new ArrayList<>();
            }
            List<Payment> loaded = new ArrayList<>();
            for (String raw : Files.readAllLines(p, StandardCharsets.UTF_8)) {
                String line = raw.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                try {
                    loaded.add(parseLine(line));
                } catch (RuntimeException ex) {
                    log.warn("Skipping malformed payment line: {}", line, ex);
                }
            }
            return loaded;
        } catch (IOException e) {
            throw new StorageException("Payment repository file operation failed", e);
        } finally {
            readLock.unlock(); // Always release the lock in a finally block
        }
    }

    /**
     * CRUD: CREATE / UPDATE / DELETE (Overwrite)
     * Saves a list of payments back out to the text file.
     * Uses a write-lock to ensure exclusive access and prevent data corruption.
     */
    public void saveAll(List<Payment> payments) {
        writeLock.lock();
        try {
            Path p = path();
            Files.createDirectories(p.getParent()); // Ensure the directory structure exists
            
            List<String> lines = new ArrayList<>();
            lines.add("# CARD|CASH,id,bookingId,amount,status"); // Write header line
            
            // Map payment objects back into strings for file writing
            lines.addAll(payments.stream().map(this::toLine).toList());
            
            // Overwrite the existing file with the updated payment data
            Files.write(p, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new StorageException("Payment repository file operation failed", e);
        } finally {
            writeLock.unlock(); // Release write lock
        }
    }

    /**
     * Factory pattern-like method to parse a single string line into a Payment object.
     */
    private Payment parseLine(String line) {
        if (line.contains("|")) {
            return parseLegacyPipePayment(line); // Handle older file formats
        }
        
        String[] a = line.split(",", -1);
        if (a.length < 5) {
            throw new IllegalArgumentException("Invalid payment record: " + line);
        }
        
        String kind = a[0].trim();
        String status = PaymentStatus.fromString(a[4].trim()).name();
        
        // Polymorphism in action: return appropriate subclass based on 'kind' field
        if (kind.equalsIgnoreCase("CARD")) {
            return new CardPayment(a[1].trim(), a[2].trim(), Double.parseDouble(a[3].trim()), status);
        }
        if (kind.equalsIgnoreCase("CASH")) {
            return new CashPayment(a[1].trim(), a[2].trim(), Double.parseDouble(a[3].trim()), status);
        }
        throw new IllegalArgumentException("Unknown payment type: " + kind);
    }

    /**
     * Method dealing with old file formatting logic.
     */
    private Payment parseLegacyPipePayment(String line) {
        String[] a = line.split("\\|", -1);
        String status = PaymentStatus.PAID.name(); // Legacy ones default to PAID
        
        // Uses enhanced switch statement (modern Java feature)
        return switch (a[0]) {
            case "CARD" -> new CardPayment(a[1], a[2], Double.parseDouble(a[3]), status);
            case "CASH" -> new CashPayment(a[1], a[2], Double.parseDouble(a[3]), status);
            default -> throw new IllegalArgumentException("Unknown payment type: " + a[0]);
        };
    }

    /**
     * Serializes a Payment object back into a comma-separated string representation.
     */
    private String toLine(Payment p) {
        // Instanceof pattern matching cleanly checks object type and performs casting (Java 16+)
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

    /**
     * Utility method to escape potentially problematic characters (like commas) in data.
     */
    private static String esc(String s) {
        return s == null ? "" : s.replace(",", ";");
    }
}
