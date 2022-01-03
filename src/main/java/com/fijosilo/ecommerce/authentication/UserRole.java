package com.fijosilo.ecommerce.authentication;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum UserRole {
    CLIENT(new HashSet<>(Arrays.asList(
            UserPermission.PRODUCT_READ
    ))),
    ADMIN(new HashSet<>(Arrays.asList(
            UserPermission.PRODUCT_READ,
            UserPermission.PRODUCT_WRITE,
            UserPermission.CLIENT_READ,
            UserPermission.CLIENT_WRITE
    )));

    private final Set<UserPermission> permissions;

    UserRole(Set<UserPermission> permissions) {
        this.permissions = permissions;
    }

    public Set<UserPermission> getPermissions() {
        return permissions;
    }

}
