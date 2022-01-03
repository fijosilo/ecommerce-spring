package com.fijosilo.ecommerce.authentication;

public enum UserPermission {
    PRODUCT_READ("product:read"),
    PRODUCT_WRITE("product:write"),
    CLIENT_READ("client:read"),
    CLIENT_WRITE("client:write");

    private final String permission;

    UserPermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }

}
