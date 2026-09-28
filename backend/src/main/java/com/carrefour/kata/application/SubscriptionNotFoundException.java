package com.carrefour.kata.application;

import java.util.UUID;

public class SubscriptionNotFoundException extends RuntimeException {

    public SubscriptionNotFoundException(UUID customerId) {
        super("No subscription found for customer: " + customerId);
    }
}
