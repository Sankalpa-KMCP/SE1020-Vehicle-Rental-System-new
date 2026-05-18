package com.sliit.vehiclerentalsystem01.review.service;

import com.sliit.vehiclerentalsystem01.review.model.CustomerReview;
import com.sliit.vehiclerentalsystem01.review.model.Feedback;
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

/**
 * CSV file persistence for {@link CustomerReview} lines (one row per review).
 */
@Service
public class ReviewFileServiceImpl implements ReviewService {

    private static final String FILE_NAME = "reviews.txt";

    @Value("${app.data.directory:data}")
    private String dataDirectory;

    private Path path() {
        return Paths.get(dataDirectory).toAbsolutePath().normalize().resolve(FILE_NAME);
    }

    @Override
    public List<Feedback> findAll() {
        return readAll();
    }

    @Override
    public Optional<Feedback> findById(String id) {
        return readAll().stream().filter(r -> id.equals(r.getReviewId())).findFirst();
    }

    @Override
    public void save(Feedback review) {
        if (!(review instanceof CustomerReview cr)) {
            throw new IllegalArgumentException("Only CustomerReview is persisted");
        }
        if (cr.getReviewId() == null || cr.getReviewId().isBlank()) {
            cr.setReviewId(UUID.randomUUID().toString());
        }
        List<Feedback> list = readAll();
        String rid = cr.getReviewId();
        list.removeIf(r -> r instanceof CustomerReview x && rid.equals(x.getReviewId()));
        list.add(cr);
        writeAll(list);
    }

    @Override
    public void deleteById(String id) {
        List<Feedback> list = readAll().stream()
                .filter(r -> !id.equals(r.getReviewId()))
                .collect(Collectors.toCollection(ArrayList::new));
        writeAll(list);
    }

    @Override
    public int countByCustomerId(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            return 0;
        }
        String key = customerId.trim();
        return (int) readAll().stream()
                .filter(r -> r instanceof CustomerReview cr && key.equalsIgnoreCase(nullSafe(cr.getReviewerName())))
                .count();
    }

    @Override
    public int countByCustomerIdAndVehicleId(String customerId, String vehicleId) {
        if (customerId == null || customerId.isBlank() || vehicleId == null || vehicleId.isBlank()) {
            return 0;
        }
        String cKey = customerId.trim();
        String vKey = vehicleId.trim();
        return (int) readAll().stream()
                .filter(r -> r instanceof CustomerReview cr
                        && cKey.equalsIgnoreCase(nullSafe(cr.getReviewerName()))
                        && vKey.equalsIgnoreCase(nullSafe(cr.getVehicleId())))
                .count();
    }

    @Override
    public int countAccountReviewsByCustomer(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            return 0;
        }
        String key = customerId.trim();
        return (int) readAll().stream()
                .filter(r -> r instanceof CustomerReview cr && key.equalsIgnoreCase(nullSafe(cr.getReviewerName()))
                        && (cr.getVehicleId() == null || cr.getVehicleId().isBlank()))
                .count();
    }

    private static String nullSafe(String s) {
        return s == null ? "" : s.trim();
    }

    private List<Feedback> readAll() {
        Path p = path();
        if (!Files.exists(p)) {
            return new ArrayList<>();
        }
        try {
            List<Feedback> loaded = new ArrayList<>();
            for (String raw : Files.readAllLines(p, StandardCharsets.UTF_8)) {
                String line = raw == null ? "" : raw.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                try {
                    loaded.add(parseLine(line));
                } catch (RuntimeException ex) {
                    // Skip corrupt rows so one bad line does not break the reviews page.
                }
            }
            return loaded;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read reviews file: " + p, e);
        }
    }

    private Feedback parseLine(String line) {
        if (line.contains("|")) {
            return parseLegacyPipeReview(line);
        }
        CustomerReview cr = CustomerReview.fromCsvLine(line);
        if (cr == null) {
            throw new IllegalArgumentException("Invalid review record: " + line);
        }
        return cr;
    }

    private Feedback parseLegacyPipeReview(String line) {
        String[] a = line.split("\\|", -1);
        if ("CUSTOMER".equals(a[0]) && a.length >= 7) {
            return new CustomerReview(
                    a[1],
                    a[3],
                    Integer.parseInt(a[5]),
                    a[6],
                    "PUBLISHED",
                    "",
                    a[4],
                    "",
                    "",
                    "");
        }
        throw new IllegalArgumentException("Unsupported legacy review line: " + line);
    }

    private String toLine(Feedback r) {
        if (r instanceof CustomerReview c) {
            return c.toCsvLine();
        }
        throw new IllegalArgumentException("Unsupported review: " + r.getClass());
    }

    private void writeAll(List<Feedback> reviews) {
        Path p = path();
        try {
            Files.createDirectories(p.getParent());
            List<String> lines = new ArrayList<>();
            lines.add("# reviewId,reviewerName,rating,comments,status,bookingId,vehicleId,staffReply,repliedBy,repliedAt");
            lines.addAll(reviews.stream().map(this::toLine).toList());
            Files.write(p, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write reviews file: " + p, e);
        }
    }
}
