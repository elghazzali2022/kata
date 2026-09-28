package com.carrefour.kata.infrastructure.persistence;

import com.carrefour.kata.domain.model.Subscription;
import com.carrefour.kata.domain.repository.SubscriptionRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class SubscriptionRepositoryAdapter implements SubscriptionRepository {

    private final SpringDataSubscriptionRepository repository;

    public SubscriptionRepositoryAdapter(
            SpringDataSubscriptionRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public Subscription save(Subscription subscription) {
        SubscriptionEntity entity = new SubscriptionEntity(
                subscription.id(),
                subscription.customerId(),
                subscription.planType(),
                subscription.startDate(),
                subscription.nextBillingDate(),
                subscription.nextBillingAdjustment()
        );

        repository.save(entity);
        return subscription;
    }

    @Override
    public Optional<Subscription> findByCustomerId(UUID customerId) {
        return repository.findByCustomerId(customerId)
                .map(entity -> {
                    Subscription subscription = new Subscription(
                            entity.getId(),
                            entity.getCustomerId(),
                            entity.getPlanType(),
                            entity.getStartDate(),
                            entity.getNextBillingDate()
                    );

                    if (entity.getNextBillingAdjustment().signum() != 0) {
                        subscription.changePlan(
                                entity.getPlanType(),
                                entity.getNextBillingAdjustment()
                        );
                    }

                    return subscription;
                });
    }
}
