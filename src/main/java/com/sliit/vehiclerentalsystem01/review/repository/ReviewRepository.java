package com.sliit.vehiclerentalsystem01.review.repository;

import com.sliit.vehiclerentalsystem01.core.exceptions.StorageException;
import com.sliit.vehiclerentalsystem01.review.entity.CustomerReview;
import com.sliit.vehiclerentalsystem01.review.entity.Feedback;
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
public class ReviewRepository {

    private static final String FILE_NAME = "reviews.txt";

    @Value("${app.data.directory:data}")
    private String dataDirectory;

    private Path path() {
        return Paths.get(dataDirectory).toAbsolutePath().normalize().resolve(FILE_NAME);
    }

    public List<Feedback> findAll() {
        Path p = path();
        if (!Files.exists(p)) {
            return new ArrayList<>();
        }
        try {
            List<Feedback> loaded = new ArrayList<>();
            for (String raw : Files.readAllLines(p, StandardCharsets.UTF_8)) {
                String line = raw.trim();
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
            throw new StorageException("Review repository file operation failed", e);
        }
    }

    public void saveAll(List<Feedback> reviews) {
        Path p = path();
        try {
            Files.createDirectories(p.getParent());
            List<String> lines = new ArrayList<>();
            lines.add("# reviewId,reviewerName,rating,comments,status,bookingId,vehicleId,staffReply,repliedBy,repliedAt");
            lines.addAll(reviews.stream().map(this::toLine).toList());
            Files.write(p, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new StorageException("Review repository file operation failed", e);
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
}
