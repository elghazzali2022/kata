package com.carrefour.kata.application;

import java.util.UUID;

public class SubscriptionAlreadyExistsException extends RuntimeException {

    public SubscriptionAlreadyExistsException(UUID customerId) {
        super("A subscription already exists for customer: " + customerId);
    }
}
