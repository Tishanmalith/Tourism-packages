package com.tourism.platform.model;

import java.io.Serializable;

/**
 * 4. Abstraction: Base abstract class for all entities in the system.
 * It provides common properties (ID) for all models.
 *
 * @param <ID> The type of the identifier (e.g., Long, String)
 */
public abstract class BaseEntity<ID> implements Serializable {

    // 1. Encapsulation: Private field with public getter and setter
    private ID id;

    public BaseEntity() {
    }

    public BaseEntity(ID id) {
        this.id = id;
    }

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }

    /**
     * 3. Polymorphism: An abstract method that can be overridden by entities
     * to provide a brief description or display name of the entity.
     */
    public abstract String getEntityDescriptor();
}
