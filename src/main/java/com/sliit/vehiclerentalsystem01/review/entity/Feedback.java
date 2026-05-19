package com.sliit.vehiclerentalsystem01.review.entity;

public abstract class Feedback {

    private String reviewId;
    private int rating;
    private String comments;
    private String status;

    public Feedback() {
        this.reviewId = "";
        this.rating = 0;
        this.comments = "";
        this.status = "";
    }

    public Feedback(String reviewId, int rating, String comments, String status) {
        this.reviewId = reviewId;
        this.rating = rating;
        this.comments = comments;
        this.status = status;
    }

    // Getters and Setters
    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public abstract String getReviewType();

    /** Alias for {@link #getReviewId()} for Thymeleaf lists. */
    public String getId() {
        return reviewId;
    }

    public void setId(String id) {
        this.reviewId = id;
    }
}
