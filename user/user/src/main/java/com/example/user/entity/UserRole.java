package com.example.user.entity;

import lombok.Getter;

@Getter
public enum UserRole {

    USER("user"),
    CUSTOMER("customer"),
    ADMIN("admin");

    private final String value;

    UserRole(String value) {
        this.value = value;
    }

}

