package com.xamarsia.simplephotosharingplatform.enums;

public enum Role {
    USER,
    ADMIN;

    public String getRole() {
        return "ROLE_" + name();
    }
}