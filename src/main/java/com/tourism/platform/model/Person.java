package com.tourism.platform.model;

import java.io.Serializable;

/**
 * 4. Abstraction: Use of an abstract class to define a template for all types of users (Person).
 * It cannot be instantiated directly.
 */
public abstract class Person<T> extends BaseEntity<T> {

    // 1. Encapsulation: Private fields with public getters and setters to protect the data.
    private String username;
    private String password;
    private String fullName;
    private String email;

    public Person() {
        super();
    }

    public Person(T id, String username, String password, String fullName, String email) {
        super(id);
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getEntityDescriptor() {
        return getRoleName() + " - " + username;
    }

    /**
     * 3. Polymorphism: Abstract method to be overridden by subclasses to provide their specific role name.
     */
    public abstract String getRoleName();
}
