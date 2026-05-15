package com.tourism.platform.model;

import java.io.Serializable;

public class Booking extends BaseEntity<Long> {

    /** References {@link User#getId()} (UUID string from users.txt). */
    private String userId;
    private Long packageId;
    private String status;
    private String bookingDate;
    private String notes;

    public Booking() {
    }

    @Override
    public String getEntityDescriptor() {
        return "Booking " + getId() + " - " + status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
