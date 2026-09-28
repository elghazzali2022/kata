package com.carrefour.kata.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProrationTest {

    @Test
    void shouldReturnZeroForSamePlan() {
        Plan monthly = new Plan(
                PlanType.MONTHLY,
                new BigDecimal("10.00"),
                30
        );

        BigDecimal result = Proration.calculate(
                monthly,
                monthly,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 31),
                LocalDate.of(2026, 1, 10)
        );

        assertEquals(new BigDecimal("0.00"), result);
    }

    @Test
    void shouldReturnZeroAtEndOfBillingPeriod() {
        Plan monthly = new Plan(
                PlanType.MONTHLY,
                new BigDecimal("10.00"),
                30
        );

        Plan annual = new Plan(
                PlanType.ANNUAL,
                new BigDecimal("96.00"),
                365
        );

        BigDecimal result = Proration.calculate(
                monthly,
                annual,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 31),
                LocalDate.of(2026, 1, 31)
        );

        assertEquals(new BigDecimal("0.00"), result);
    }

    @Test
    void shouldCalculatePositiveAdjustmentWhenChangingToMoreExpensivePlan() {
        Plan monthly = new Plan(
                PlanType.MONTHLY,
                new BigDecimal("10.00"),
                30
        );

        Plan annual = new Plan(
                PlanType.ANNUAL,
                new BigDecimal("96.00"),
                365
        );

        BigDecimal result = Proration.calculate(
                monthly,
                annual,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 31),
                LocalDate.of(2026, 1, 16)
        );

        assertEquals(
                new BigDecimal("-1.05"),
                result
        );
    }
}
