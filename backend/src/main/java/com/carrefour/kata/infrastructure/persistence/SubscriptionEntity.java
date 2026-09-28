package com.carrefour.kata.infrastructure.persistence;

import com.carrefour.kata.domain.model.PlanType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "subscriptions")
public class SubscriptionEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanType planType;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate nextBillingDate;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal nextBillingAdjustment;

    protected SubscriptionEntity() {
    }

    public SubscriptionEntity(
            UUID id,
            UUID customerId,
            PlanType planType,
            LocalDate startDate,
            LocalDate nextBillingDate,
            BigDecimal nextBillingAdjustment
    ) {
        this.id = id;
        this.customerId = customerId;
        this.planType = planType;
        this.startDate = startDate;
        this.nextBillingDate = nextBillingDate;
        this.nextBillingAdjustment = nextBillingAdjustment;
    }

    public UUID getId() {
        return id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public PlanType getPlanType() {
        return planType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getNextBillingDate() {
        return nextBillingDate;
    }

    public BigDecimal getNextBillingAdjustment() {
        return nextBillingAdjustment;
    }
}
