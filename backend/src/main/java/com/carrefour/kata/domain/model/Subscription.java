package com.carrefour.kata.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class Subscription {

    private final UUID id;
    private final UUID customerId;
    private final LocalDate startDate;
    private final LocalDate nextBillingDate;

    private PlanType planType;
    private BigDecimal nextBillingAdjustment;

    public Subscription(
            UUID id,
            UUID customerId,
            PlanType planType,
            LocalDate startDate,
            LocalDate nextBillingDate
    ) {
        this.id = id;
        this.customerId = customerId;
        this.planType = planType;
        this.startDate = startDate;
        this.nextBillingDate = nextBillingDate;
        this.nextBillingAdjustment = BigDecimal.ZERO.setScale(2);
    }

    public void changePlan(PlanType newPlanType, BigDecimal adjustment) {
        this.planType = newPlanType;
        this.nextBillingAdjustment = this.nextBillingAdjustment
                .add(adjustment)
                .setScale(2);
    }

    public UUID id() {
        return id;
    }

    public UUID customerId() {
        return customerId;
    }

    public PlanType planType() {
        return planType;
    }

    public LocalDate startDate() {
        return startDate;
    }

    public LocalDate nextBillingDate() {
        return nextBillingDate;
    }

    public BigDecimal nextBillingAdjustment() {
        return nextBillingAdjustment;
    }
}
