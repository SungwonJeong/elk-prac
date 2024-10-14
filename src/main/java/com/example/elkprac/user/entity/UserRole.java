package com.example.elkprac.user.entity;

import lombok.Getter;

@Getter
public enum UserRole {

    ADMIN(Authority.ADMIN),
    SELLER(Authority.SELLER),
    USER(Authority.USER);

    private final String authority;

    UserRole(String authority) {
        this.authority = authority;
    }

    public static class Authority {
        public static final String ADMIN = "ROLE_ADMIN";
        public static final String SELLER = "ROLE_SELLER";
        public static final String USER = "ROLE_USER";
    }
}
