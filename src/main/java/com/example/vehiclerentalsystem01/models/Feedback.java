package com.example.vehiclerentalsystem01.models;

/**
 * OOP Concept: Abstraction.
 * Abstract base class for all system feedback.
 */
public abstract class Feedback {
    // OOP Concept: Encapsulation
    private String reviewId;
    private int rating; // 1 to 5 stars
    private String comments;
    private String status; // PUBLISHED or FLAGGED

    public Feedback(String reviewId, int rating, String comments, String status) {
        this.reviewId = reviewId;
        this.rating = rating;
        this.comments = comments;
        this.status = status;
    }

    // OOP Concept: Abstraction (Forces child classes to define review type)
    public abstract String getReviewType();

    // Getters and Setters
    public String getReviewId() { return reviewId; }
    public void setReviewId(String reviewId) { this.reviewId = reviewId; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}