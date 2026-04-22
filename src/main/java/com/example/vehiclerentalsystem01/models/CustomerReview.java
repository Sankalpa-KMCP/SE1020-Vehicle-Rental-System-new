package com.example.vehiclerentalsystem01.models;

/**
 * OOP Concept: Inheritance.
 * Inherits the core feedback properties and adds specific customer details.
 */
public class CustomerReview extends Feedback {
    private String reviewerName;
    private String bookingId;
    private String staffReply;
    private String repliedBy;
    private String repliedAt;

    public CustomerReview(String reviewId, String reviewerName, int rating, String comments, String status) {
        this(reviewId, reviewerName, rating, comments, status, "", "", "", "");
    }

    public CustomerReview(String reviewId, String reviewerName, int rating, String comments, String status,
                          String staffReply, String repliedBy, String repliedAt) {
        this(reviewId, reviewerName, rating, comments, status, "", staffReply, repliedBy, repliedAt);
    }

    public CustomerReview(String reviewId, String reviewerName, int rating, String comments, String status,
                          String bookingId, String staffReply, String repliedBy, String repliedAt) {
        super(reviewId, rating, comments, status);
        this.reviewerName = reviewerName;
        this.bookingId = bookingId == null ? "" : bookingId;
        this.staffReply = staffReply == null ? "" : staffReply;
        this.repliedBy = repliedBy == null ? "" : repliedBy;
        this.repliedAt = repliedAt == null ? "" : repliedAt;
    }

    public String getReviewerName() { return reviewerName; }
    public void setReviewerName(String reviewerName) { this.reviewerName = reviewerName; }
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId == null ? "" : bookingId; }
    public String getStaffReply() { return staffReply; }
    public void setStaffReply(String staffReply) { this.staffReply = staffReply == null ? "" : staffReply; }
    public String getRepliedBy() { return repliedBy; }
    public void setRepliedBy(String repliedBy) { this.repliedBy = repliedBy == null ? "" : repliedBy; }
    public String getRepliedAt() { return repliedAt; }
    public void setRepliedAt(String repliedAt) { this.repliedAt = repliedAt == null ? "" : repliedAt; }

    /**
     * OOP Concept: Polymorphism.
     */
    @Override
    public String getReviewType() {
        return "Verified Customer";
    }
}