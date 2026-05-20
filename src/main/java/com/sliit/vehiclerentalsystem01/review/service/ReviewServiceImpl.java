package com.sliit.vehiclerentalsystem01.review.service;

import com.sliit.vehiclerentalsystem01.core.exceptions.ValidationException;
import com.sliit.vehiclerentalsystem01.review.entity.CustomerReview;
import com.sliit.vehiclerentalsystem01.review.entity.Feedback;
import com.sliit.vehiclerentalsystem01.review.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @jakarta.annotation.PostConstruct
    public void cleanupOrphanedImages() {
        try {
            java.io.File dir = new java.io.File("uploads/reviews");
            if (!dir.exists() || !dir.isDirectory()) {
                return;
            }
            java.io.File[] files = dir.listFiles();
            if (files == null || files.length == 0) {
                return;
            }
            java.util.Set<String> activeImages = findAll().stream()
                    .filter(r -> r instanceof CustomerReview)
                    .map(r -> ((CustomerReview) r).getImageUrl())
                    .filter(url -> url != null && !url.isBlank())
                    .map(url -> url.startsWith("/") ? url.substring(1) : url)
                    .map(url -> java.nio.file.Paths.get(url).getFileName().toString())
                    .collect(Collectors.toSet());

            for (java.io.File file : files) {
                if (file.isFile() && !activeImages.contains(file.getName())) {
                    java.nio.file.Files.deleteIfExists(file.toPath());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Feedback> findAll() {
        return reviewRepository.findAll();
    }

    @Override
    public Optional<Feedback> findById(String id) {
        return reviewRepository.findAll().stream().filter(r -> id.equals(r.getReviewId())).findFirst();
    }

    @Override
    public void save(Feedback review) {
        if (!(review instanceof CustomerReview cr)) {
            throw new IllegalArgumentException("Only CustomerReview is persisted");
        }
        int rating = cr.getRating();
        if (rating < 1 || rating > 5) {
            throw new ValidationException("invalid_rating");
        }
        if (cr.getReviewId() == null || cr.getReviewId().isBlank()) {
            cr.setReviewId(UUID.randomUUID().toString());
        }
        List<Feedback> list = new ArrayList<>(reviewRepository.findAll());
        String rid = cr.getReviewId();
        list.removeIf(r -> r instanceof CustomerReview x && rid.equals(x.getReviewId()));
        list.add(cr);
        reviewRepository.saveAll(list);
    }

    @Override
    public void deleteById(String id) {
        findById(id).ifPresent(f -> {
            if (f instanceof CustomerReview cr) {
                String imageUrl = cr.getImageUrl();
                if (imageUrl != null && !imageUrl.isBlank()) {
                    try {
                        String pathStr = imageUrl.startsWith("/") ? imageUrl.substring(1) : imageUrl;
                        java.nio.file.Path path = java.nio.file.Paths.get(pathStr);
                        java.nio.file.Files.deleteIfExists(path);
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        List<Feedback> list = reviewRepository.findAll().stream()
                .filter(r -> !id.equals(r.getReviewId()))
                .collect(Collectors.toCollection(ArrayList::new));
        reviewRepository.saveAll(list);
    }

    @Override
    public int countByCustomerId(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            return 0;
        }
        String key = customerId.trim();
        return (int) reviewRepository.findAll().stream()
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
        return (int) reviewRepository.findAll().stream()
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
        return (int) reviewRepository.findAll().stream()
                .filter(r -> r instanceof CustomerReview cr && key.equalsIgnoreCase(nullSafe(cr.getReviewerName()))
                        && (cr.getVehicleId() == null || cr.getVehicleId().isBlank()))
                .count();
    }

    private static String nullSafe(String s) {
        return s == null ? "" : s.trim();
    }
}
