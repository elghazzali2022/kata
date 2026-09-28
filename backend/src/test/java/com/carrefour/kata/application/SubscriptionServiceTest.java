package com.carrefour.kata.application;

import com.carrefour.kata.domain.model.Plan;
import com.carrefour.kata.domain.model.PlanType;
import com.carrefour.kata.domain.model.Subscription;
import com.carrefour.kata.domain.repository.SubscriptionRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SubscriptionServiceTest {

    private final UUID customerId =
            UUID.fromString("11111111-1111-1111-1111-111111111111");

    private final Clock clock = Clock.fixed(
            Instant.parse("2026-01-10T00:00:00Z"),
            ZoneOffset.UTC
    );

    private final Map<PlanType, Plan> plans = Map.of(
            PlanType.MONTHLY,
            new Plan(PlanType.MONTHLY, new BigDecimal("10.00"), 30),
            PlanType.QUARTERLY,
            new Plan(PlanType.QUARTERLY, new BigDecimal("27.00"), 90),
            PlanType.ANNUAL,
            new Plan(PlanType.ANNUAL, new BigDecimal("96.00"), 365)
    );

    @Test
    void shouldCreateMonthlySubscription() {
        InMemoryRepository repository = new InMemoryRepository();
        SubscriptionService service = new SubscriptionService(
                repository,
                plans::get,
                clock
        );

        Subscription result = service.subscribe(
                customerId,
                PlanType.MONTHLY
        );

        assertEquals(PlanType.MONTHLY, result.planType());
        assertEquals(
                LocalDate.of(2026, 2, 9),
                result.nextBillingDate()
        );
        assertEquals(new BigDecimal("0.00"), result.nextBillingAdjustment());
    }

    @Test
    void shouldRejectDuplicateSubscription() {
        InMemoryRepository repository = new InMemoryRepository();
        SubscriptionService service = new SubscriptionService(
                repository,
                plans::get,
                clock
        );

        service.subscribe(customerId, PlanType.MONTHLY);

        assertThrows(
                SubscriptionAlreadyExistsException.class,
                () -> service.subscribe(customerId, PlanType.ANNUAL)
        );
    }

    @Test
    void shouldChangePlanAndStoreAdjustment() {
        InMemoryRepository repository = new InMemoryRepository();
        SubscriptionService service = new SubscriptionService(
                repository,
                plans::get,
                clock
        );

        Subscription subscription =
                service.subscribe(customerId, PlanType.MONTHLY);

        // Persist a subscription whose billing period starts on Jan 10.
        repository.save(subscription);

        Subscription changed =
                service.changePlan(customerId, PlanType.ANNUAL);

        assertEquals(PlanType.ANNUAL, changed.planType());
        assertNotEquals(
                BigDecimal.ZERO,
                changed.nextBillingAdjustment()
        );
    }

    private static class InMemoryRepository
            implements SubscriptionRepository {

        private final Map<UUID, Subscription> subscriptions =
                new HashMap<>();

        @Override
        public Subscription save(Subscription subscription) {
            subscriptions.put(subscription.customerId(), subscription);
            return subscription;
        }

        @Override
        public Optional<Subscription> findByCustomerId(UUID customerId) {
            return Optional.ofNullable(subscriptions.get(customerId));
        }
    }
}
