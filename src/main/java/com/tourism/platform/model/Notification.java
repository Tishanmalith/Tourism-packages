package com.tourism.platform.model;

import java.io.Serializable;

/**
 * In-app notification for a customer.
 * File: notifications.txt
 * Format: id,userId,bookingId,message,read,createdAt
 */
public class Notification extends BaseEntity<Long> {

    /** References {@link User#getId()}. */
    private String userId;
    /** References {@link Booking#getId()}. */
    private Long bookingId;
    private String message;
    /** "true" or "false" */
    private boolean read;
    /** ISO date-time string, e.g. 2026-05-13T15:00:00 */
    private String createdAt;

    public Notification() {
    }

    @Override
    public String getEntityDescriptor() {
        return "Notification for User " + userId;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
