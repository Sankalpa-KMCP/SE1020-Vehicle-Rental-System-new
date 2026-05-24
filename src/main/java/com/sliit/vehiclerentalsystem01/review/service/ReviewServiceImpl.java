package com.sliit.vehiclerentalsystem01.review.service;

import com.sliit.vehiclerentalsystem01.core.config.CachingConfig;
import com.sliit.vehiclerentalsystem01.core.exceptions.ValidationException;
import com.sliit.vehiclerentalsystem01.review.entity.CustomerReview;
import com.sliit.vehiclerentalsystem01.review.entity.Feedback;
import com.sliit.vehiclerentalsystem01.review.repository.ReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    private static final Logger log = LoggerFactory.getLogger(ReviewServiceImpl.class);

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
                    log.info("Cleaned orphaned review image: {}", file.getName());
                }
            }
        } catch (Exception e) {
            log.warn("Failed to clean orphaned review images", e);
        }
    }

    @Override
    @Cacheable(CachingConfig.REVIEWS_CACHE)
    public List<Feedback> findAll() {
        return reviewRepository.findAll();
    }

    @Override
    public Optional<Feedback> findById(String id) {
        return reviewRepository.findAll().stream().filter(r -> id.equals(r.getReviewId())).findFirst();
    }

    @Override
    @CacheEvict(value = CachingConfig.REVIEWS_CACHE, allEntries = true)
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
        list.removeIf(r -> {
            if (r instanceof CustomerReview c) return rid.equals(c.getReviewId());
            return false;
        });
        list.add(cr);
        reviewRepository.saveAll(list);
    }

    @Override
    @CacheEvict(value = CachingConfig.REVIEWS_CACHE, allEntries = true)
    public void deleteById(String id) {
        findById(id).ifPresent(f -> {
            if (f instanceof CustomerReview cr) {
                String imageUrl = cr.getImageUrl();
                if (imageUrl != null && !imageUrl.isBlank()) {
                    try {
                        java.nio.file.Path target = sanitizeAndResolve(imageUrl, "uploads/reviews");
                        java.nio.file.Files.deleteIfExists(target);
                        log.info("Deleted review image: {}", target);
                    } catch (java.io.IOException e) {
                        log.warn("Failed to delete review image: {}", imageUrl, e);
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

    private static java.nio.file.Path sanitizeAndResolve(String userPath, String baseDir) throws java.io.IOException {
        java.nio.file.Path base = java.nio.file.Paths.get(baseDir).toAbsolutePath().normalize();
        String clean = userPath.startsWith("/") ? userPath.substring(1) : userPath;
        java.nio.file.Path resolved = base.resolve(clean).normalize();
        if (!resolved.startsWith(base)) {
            throw new java.nio.file.AccessDeniedException("Blocked path traversal: " + userPath);
        }
        return resolved;
    }
}
