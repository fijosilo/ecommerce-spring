package com.fijosilo.ecommerce.order;

public enum PaymentMethod {
    PAYPAL("PAYPAL"),
    CREDIT_CARD("CREDIT_CARD");

    private final String value;

    PaymentMethod(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

}
