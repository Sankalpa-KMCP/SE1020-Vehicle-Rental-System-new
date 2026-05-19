package com.sliit.vehiclerentalsystem01.review.entity;

/**
 * Customer-submitted review. Optional {@code vehicleId} supports counts when tied to a vehicle;
 * blank {@code vehicleId} counts as an account-only review for {@code countAccountReviewsByCustomer}.
 */
public class CustomerReview extends Feedback {

    private String reviewerName;
    private String bookingId;
    /** Optional; may be empty for account-level reviews. */
    private String vehicleId;
    private String staffReply;
    private String repliedBy;
    private String repliedAt;

    public CustomerReview() {
        super();
        this.reviewerName = "";
        this.bookingId = "";
        this.vehicleId = "";
        this.staffReply = "";
        this.repliedBy = "";
        this.repliedAt = "";
    }

    public CustomerReview(String reviewId, String reviewerName, int rating, String comments, String status) {
        this(reviewId, reviewerName, rating, comments, status, "", "", "", "", "");
    }

    public CustomerReview(String reviewId, String reviewerName, int rating, String comments, String status,
                          String staffReply, String repliedBy, String repliedAt) {
        this(reviewId, reviewerName, rating, comments, status, "", "", staffReply, repliedBy, repliedAt);
    }

    public CustomerReview(String reviewId, String reviewerName, int rating, String comments, String status,
                          String bookingId, String staffReply, String repliedBy, String repliedAt) {
        this(reviewId, reviewerName, rating, comments, status, bookingId, "", staffReply, repliedBy, repliedAt);
    }

    public CustomerReview(String reviewId, String reviewerName, int rating, String comments, String status,
                          String bookingId, String vehicleId, String staffReply, String repliedBy, String repliedAt) {
        super(reviewId, rating, comments, status);
        this.reviewerName = reviewerName;
        this.bookingId = bookingId == null ? "" : bookingId;
        this.vehicleId = vehicleId == null ? "" : vehicleId;
        this.staffReply = staffReply == null ? "" : staffReply;
        this.repliedBy = repliedBy == null ? "" : repliedBy;
        this.repliedAt = repliedAt == null ? "" : repliedAt;
    }

    // Getters and Setters
    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getStaffReply() {
        return staffReply;
    }

    public void setStaffReply(String staffReply) {
        this.staffReply = staffReply;
    }

    public String getRepliedBy() {
        return repliedBy;
    }

    public void setRepliedBy(String repliedBy) {
        this.repliedBy = repliedBy;
    }

    public String getRepliedAt() {
        return repliedAt;
    }

    public void setRepliedAt(String repliedAt) {
        this.repliedAt = repliedAt;
    }

    @Override
    public String getReviewType() {
        return "Verified Customer";
    }

    /** Thymeleaf / form alias for {@link #getComments()}. */
    public String getComment() {
        return getComments();
    }

    public void setComment(String comment) {
        setComments(comment);
    }

    /** Alias for {@link #getReviewerName()} when matching stored customer username. */
    public String getCustomerId() {
        return reviewerName;
    }

    public void setCustomerId(String customerId) {
        this.reviewerName = customerId;
    }

    public String toCsvLine() {
        return String.join(",",
                escape(getReviewId()),
                escape(reviewerName),
                Integer.toString(getRating()),
                escape(getComments()),
                escape(getStatus()),
                escape(bookingId),
                escape(vehicleId),
                escape(staffReply),
                escape(repliedBy),
                escape(repliedAt));
    }

    private static String escape(String s) {
        if (s == null) {
            return "";
        }
        return s.replace(",", ";");
    }

    public static CustomerReview fromCsvLine(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }
        String[] a = line.split(",", -1);
        if (a.length < 10) {
            return null;
        }
        return new CustomerReview(
                a[0].trim(),
                a[1].trim(),
                Integer.parseInt(a[2].trim()),
                a[3].trim(),
                a[4].trim(),
                a[5].trim(),
                a[6].trim(),
                a[7].trim(),
                a[8].trim(),
                a[9].trim());
    }
}
