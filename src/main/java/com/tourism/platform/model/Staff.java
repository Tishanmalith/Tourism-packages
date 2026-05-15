package com.tourism.platform.model;

import java.io.Serializable;

public class Staff extends Person<Long> {

    // 2. Inheritance: Staff inherits id, username, password, fullName, and email from Person.

    private String department;

    public Staff() {
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    /**
     * 3. Polymorphism: Overriding the abstract method from Person.
     */
    @Override
    public String getRoleName() {
        return "STAFF";
    }
}
