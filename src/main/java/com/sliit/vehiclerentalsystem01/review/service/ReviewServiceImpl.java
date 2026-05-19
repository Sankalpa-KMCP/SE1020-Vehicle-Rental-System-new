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
