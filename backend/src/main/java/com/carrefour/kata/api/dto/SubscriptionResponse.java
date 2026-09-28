package com.carrefour.kata.api.dto;

import com.carrefour.kata.domain.model.PlanType;
import com.carrefour.kata.domain.model.Subscription;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record SubscriptionResponse(
        UUID id,
        UUID customerId,
        PlanType plan,
        LocalDate startDate,
        LocalDate nextBillingDate,
        BigDecimal nextBillingAdjustment
) {

    public static SubscriptionResponse from(Subscription subscription) {
        return new SubscriptionResponse(
                subscription.id(),
                subscription.customerId(),
                subscription.planType(),
                subscription.startDate(),
                subscription.nextBillingDate(),
                subscription.nextBillingAdjustment()
        );
    }
}
