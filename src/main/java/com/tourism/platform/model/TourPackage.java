package com.tourism.platform.model;

import java.io.Serializable;

public class TourPackage extends BaseEntity<Long> {

    private String name;
    private String description;
    private double price;
    private int durationDays;
    private Long destinationId;

    public TourPackage() {
        this.durationDays = 7;
    }

    @Override
    public String getEntityDescriptor() {
        return name + " - " + durationDays + " days";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(int durationDays) {
        this.durationDays = durationDays;
    }

    public Long getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(Long destinationId) {
        this.destinationId = destinationId;
    }
}
