package com.xamarsia.simplephotosharingplatform;

/**
 * Roles for the User class
 */
public enum Role {
    USER,
    ADMIN;

    public String getRole() {
        return "ROLE_" + name();
    }
}