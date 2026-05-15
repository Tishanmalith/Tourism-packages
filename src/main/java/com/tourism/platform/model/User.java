package com.tourism.platform.model;

import java.io.Serializable;

public class User extends Person<String> {

    // 2. Inheritance: User inherits id, username, password, fullName, and email from Person.

    private String phone;
    /** e.g. Passenger (file column 6). */
    private String userType;

    public User() {
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    /**
     * 3. Polymorphism: Overriding the abstract method from Person to return the specific role.
     */
    @Override
    public String getRoleName() {
        return "USER";
    }
}
