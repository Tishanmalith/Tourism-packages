package com.tourism.platform.model;

import java.io.Serializable;

public class Destination extends BaseEntity<Long> {

    private String name;
    private String country;
    private String description;

    public Destination() {
    }

    @Override
    public String getEntityDescriptor() {
        return name + " (" + country + ")";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
