package com.xamarsia.simplephotosharingplatform.user;

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