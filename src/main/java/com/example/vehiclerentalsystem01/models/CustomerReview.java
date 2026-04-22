package com.example.vehiclerentalsystem01.models;

/**
 * OOP Concept: Inheritance.
 * Inherits the core feedback properties and adds specific customer details.
 */
public class CustomerReview extends Feedback {
    private String reviewerName;

    public CustomerReview(String reviewId, String reviewerName, int rating, String comments, String status) {
        super(reviewId, rating, comments, status);
        this.reviewerName = reviewerName;
    }

    public String getReviewerName() { return reviewerName; }
    public void setReviewerName(String reviewerName) { this.reviewerName = reviewerName; }

    /**
     * OOP Concept: Polymorphism.
     */
    @Override
    public String getReviewType() {
        return "Verified Customer";
    }
}