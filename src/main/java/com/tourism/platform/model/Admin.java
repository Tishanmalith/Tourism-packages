package com.tourism.platform.model;

import java.io.Serializable;

public class Admin extends Person<Long> {

    // 2. Inheritance: Admin inherits id, username, password, fullName, and email from Person.

    public Admin() {
    }

    public Admin(Long id, String username, String password, String fullName, String email) {
        super(id, username, password, fullName, email);
    }

    /**
     * 3. Polymorphism: Overriding the abstract method from Person.
     */
    @Override
    public String getRoleName() {
        return "ADMIN";
    }
}
