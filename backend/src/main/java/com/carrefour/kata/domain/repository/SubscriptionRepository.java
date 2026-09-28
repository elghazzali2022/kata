package com.carrefour.kata.domain.repository;

import com.carrefour.kata.domain.model.Subscription;

import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository {

    Subscription save(Subscription subscription);

    Optional<Subscription> findByCustomerId(UUID customerId);
}
