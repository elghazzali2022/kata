package com.carrefour.kata.infrastructure.persistence;

import com.carrefour.kata.application.port.out.RefundRepository;
import com.carrefour.kata.domain.model.RefundRequest;
import org.springframework.stereotype.Repository;

@Repository
public class JpaRefundRepository implements RefundRepository {
    private final SpringDataRefundRepository repository;

    public JpaRefundRepository(SpringDataRefundRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean existsByOrderIdAndItemId(String orderId, String itemId) {
        return repository.existsByOrderIdAndItemId(orderId, itemId);
    }

    @Override
    public RefundRequest save(RefundRequest refundRequest) {
        return repository.save(RefundEntity.fromDomain(refundRequest)).toDomain();
    }
}

