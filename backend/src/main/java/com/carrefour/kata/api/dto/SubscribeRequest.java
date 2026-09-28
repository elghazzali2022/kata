package com.carrefour.kata.api.dto;

import com.carrefour.kata.domain.model.PlanType;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SubscribeRequest(
        @NotNull UUID customerId,
        @NotNull PlanType plan
) {
}
