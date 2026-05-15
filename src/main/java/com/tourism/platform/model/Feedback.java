package com.tourism.platform.model;

import java.io.Serializable;

public class Feedback extends BaseEntity<Long> {

    /** References {@link User#getId()} (UUID string). */
    private String userId;
    private Long packageId;
    private int rating;
    private String comment;
    private String createdAt;

    public Feedback() {
    }

    @Override
    public String getEntityDescriptor() {
        return "Feedback " + getId() + " - Rating: " + rating;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getPackageId() {
        return packageId;
    }

    public void setPackageId(Long packageId) {
        this.packageId = packageId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
