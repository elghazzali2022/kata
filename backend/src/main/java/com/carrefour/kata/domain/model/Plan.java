package com.carrefour.kata.domain.model;

import java.math.BigDecimal;

public record Plan(
        PlanType type,
        BigDecimal price,
        int durationInDays
) {
    public Plan {
        if (price.signum() < 0) {
            throw new IllegalArgumentException("Plan price cannot be negative");
        }
        if (durationInDays <= 0) {
            throw new IllegalArgumentException("Plan duration must be positive");
        }
    }
}
