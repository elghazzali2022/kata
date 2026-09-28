package com.carrefour.kata.application;

import com.carrefour.kata.domain.model.Plan;
import com.carrefour.kata.domain.model.PlanType;
import com.carrefour.kata.domain.model.Proration;
import com.carrefour.kata.domain.model.Subscription;
import com.carrefour.kata.domain.repository.SubscriptionRepository;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.UUID;

public class SubscriptionService {

    private final SubscriptionRepository repository;
    private final PlanProvider planProvider;
    private final Clock clock;

    public SubscriptionService(
            SubscriptionRepository repository,
            PlanProvider planProvider,
            Clock clock
    ) {
        this.repository = repository;
        this.planProvider = planProvider;
        this.clock = clock;
    }

    public Subscription subscribe(UUID customerId, PlanType planType) {
        repository.findByCustomerId(customerId)
                .ifPresent(existing -> {
                    throw new SubscriptionAlreadyExistsException(customerId);
                });

        Plan plan = planProvider.get(planType);
        LocalDate today = LocalDate.now(clock);

        Subscription subscription = new Subscription(
                UUID.randomUUID(),
                customerId,
                planType,
                today,
                today.plusDays(plan.durationInDays())
        );

        return repository.save(subscription);
    }

    public Subscription get(UUID customerId) {
        return repository.findByCustomerId(customerId)
                .orElseThrow(() -> new SubscriptionNotFoundException(customerId));
    }

    public Subscription changePlan(UUID customerId, PlanType newPlanType) {
        Subscription subscription = get(customerId);

        Plan currentPlan = planProvider.get(subscription.planType());
        Plan newPlan = planProvider.get(newPlanType);

        BigDecimal adjustment = Proration.calculate(
                currentPlan,
                newPlan,
                subscription.startDate(),
                subscription.nextBillingDate(),
                LocalDate.now(clock)
        );

        subscription.changePlan(newPlanType, adjustment);

        return repository.save(subscription);
    }
}
