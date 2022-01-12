package com.fijosilo.ecommerce.address;

public enum AddressPurpose {
    CHARGE("CHARGE"),
    DELIVER("DELIVER");

    private final String value;

    AddressPurpose(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

}
