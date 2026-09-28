package com.carrefour.kata.api.dto;

import com.carrefour.kata.domain.model.PlanType;
import jakarta.validation.constraints.NotNull;

public record ChangePlanRequest(
        @NotNull PlanType plan
) {
}
