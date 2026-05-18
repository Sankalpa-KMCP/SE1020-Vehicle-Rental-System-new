package com.sliit.vehiclerentalsystem01.review.service;

import com.sliit.vehiclerentalsystem01.review.model.Feedback;

import java.util.List;
import java.util.Optional;

public interface ReviewService {

    List<Feedback> findAll();

    Optional<Feedback> findById(String id);

    void save(Feedback review);

    void deleteById(String id);

    int countByCustomerId(String customerId);

    int countByCustomerIdAndVehicleId(String customerId, String vehicleId);

    int countAccountReviewsByCustomer(String customerId);
}
