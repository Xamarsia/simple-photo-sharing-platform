package com.xamarsia.simplephotosharingplatform.user;


public enum Role {
    USER,
    ADMIN;

    public String getRole() {
        return "ROLE_" + name();
    }
}