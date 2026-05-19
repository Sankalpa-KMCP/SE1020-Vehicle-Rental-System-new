package com.sliit.vehiclerentalsystem01.payment.repository;

import com.sliit.vehiclerentalsystem01.core.exceptions.StorageException;
import com.sliit.vehiclerentalsystem01.payment.entity.CardPayment;
import com.sliit.vehiclerentalsystem01.payment.entity.CashPayment;
import com.sliit.vehiclerentalsystem01.payment.entity.Payment;
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

@Repository
public class PaymentRepository {

    private static final String FILE_NAME = "payments.txt";
    private static final String FILE_HEADER = "# CARD|CASH,id,bookingId,amount,status";
    private static final String DEFAULT_LEGACY_PAYMENT_STATUS = "PAID";

    @Value("${app.data.directory:data}")
    private String dataDirectory;

    private Path path() {
        return Paths.get(dataDirectory).toAbsolutePath().normalize().resolve(FILE_NAME);
    }

    public List<Payment> findAll() {
        Path paymentFilePath = path();

        if (!Files.exists(paymentFilePath)) {
            return new ArrayList<>();
        }

        try {
            List<String> fileLines = Files.readAllLines(paymentFilePath, StandardCharsets.UTF_8);
            List<Payment> payments = new ArrayList<>();

            for (String fileLine : fileLines) {
                if (isDataLine(fileLine)) {
                    Payment payment = parseLine(fileLine.trim());
                    payments.add(payment);
                }
            }

            return payments;
        } catch (IOException e) {
            throw new StorageException("Payment repository file operation failed", e);
        }
    }

    public void saveAll(List<Payment> payments) {
        Path paymentFilePath = path();

        try {
            Files.createDirectories(paymentFilePath.getParent());

            List<String> fileLines = convertPaymentsToFileLines(payments);

            Files.write(paymentFilePath, fileLines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new StorageException("Payment repository file operation failed", e);
        }
    }

    private boolean isDataLine(String line) {
        if (line == null) {
            return false;
        }

        String trimmedLine = line.trim();

        if (trimmedLine.isEmpty()) {
            return false;
        }

        return !trimmedLine.startsWith("#");
    }

    private List<String> convertPaymentsToFileLines(List<Payment> payments) {
        List<String> fileLines = new ArrayList<>();
        fileLines.add(FILE_HEADER);

        for (Payment payment : payments) {
            fileLines.add(toLine(payment));
        }

        return fileLines;
    }

    private Payment parseLine(String line) {
        if (isLegacyPipeRecord(line)) {
            return parseLegacyPipePayment(line);
        }

        String[] columns = line.split(",", -1);
        validateModernPaymentColumnCount(columns, line);

        String paymentType = columns[0].trim();
        String paymentId = columns[1].trim();
        String bookingId = columns[2].trim();
        double amount = Double.parseDouble(columns[3].trim());
        String status = columns[4].trim();

        return createPayment(paymentType, paymentId, bookingId, amount, status);
    }

    private boolean isLegacyPipeRecord(String line) {
        return line.contains("|");
    }

    private void validateModernPaymentColumnCount(String[] columns, String originalLine) {
        if (columns.length < 5) {
            throw new IllegalArgumentException("Invalid payment record: " + originalLine);
        }
    }

    private Payment createPayment(String paymentType,
                                  String paymentId,
                                  String bookingId,
                                  double amount,
                                  String status) {
        if (paymentType.equalsIgnoreCase("CARD")) {
            return new CardPayment(paymentId, bookingId, amount, status);
        }

        if (paymentType.equalsIgnoreCase("CASH")) {
            return new CashPayment(paymentId, bookingId, amount, status);
        }

        throw new IllegalArgumentException("Unknown payment type: " + paymentType);
    }

    private Payment parseLegacyPipePayment(String line) {
        String[] columns = line.split("\\|", -1);
        validateLegacyPaymentColumnCount(columns, line);

        String paymentType = columns[0];
        String paymentId = columns[1];
        String bookingId = columns[2];
        double amount = Double.parseDouble(columns[3]);

        return createLegacyPayment(paymentType, paymentId, bookingId, amount);
    }

    private void validateLegacyPaymentColumnCount(String[] columns, String originalLine) {
        if (columns.length < 4) {
            throw new IllegalArgumentException("Invalid payment record: " + originalLine);
        }
    }

    private Payment createLegacyPayment(String paymentType, String paymentId, String bookingId, double amount) {
        if ("CARD".equals(paymentType)) {
            return new CardPayment(paymentId, bookingId, amount, DEFAULT_LEGACY_PAYMENT_STATUS);
        }

        if ("CASH".equals(paymentType)) {
            return new CashPayment(paymentId, bookingId, amount, DEFAULT_LEGACY_PAYMENT_STATUS);
        }

        throw new IllegalArgumentException("Unknown payment type: " + paymentType);
    }

    private String toLine(Payment payment) {
        if (payment instanceof CardPayment cardPayment) {
            return toModernCsvLine("CARD", cardPayment);
        }

        if (payment instanceof CashPayment cashPayment) {
            return toModernCsvLine("CASH", cashPayment);
        }

        throw new IllegalArgumentException("Unsupported payment: " + payment.getClass());
    }

    private String toModernCsvLine(String paymentType, Payment payment) {
        return String.join(",",
                paymentType,
                esc(payment.getId()),
                esc(payment.getBookingId()),
                Double.toString(payment.getAmount()),
                esc(payment.getStatus()));
    }

    private static String esc(String value) {
        return value == null ? "" : value.replace(",", ";");
    }
}
