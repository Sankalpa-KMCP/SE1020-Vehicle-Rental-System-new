package com.sliit.vehiclerentalsystem01.review.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public abstract class Feedback {

    private String reviewId;
    private int rating;
    private String comments;
    private String status;

    public Feedback(String reviewId, int rating, String comments, String status) {
        this.reviewId = reviewId;
        this.rating = rating;
        this.comments = comments;
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
