package com.kata.carrefour.infrastructure.persistence;

import com.kata.carrefour.application.port.out.RefundRepository;
import com.kata.carrefour.domain.model.RefundRequest;
import org.springframework.stereotype.Repository;

@Repository
public class JpaRefundRepository implements RefundRepository {

    private final SpringDataRefundRepository repository;

    public JpaRefundRepository(
            SpringDataRefundRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public boolean existsByOrderIdAndItemId(
            String orderId,
            String itemId
    ) {
        return repository.existsByOrderIdAndItemId(
                orderId,
                itemId
        );
    }

    @Override
    public RefundRequest save(
            RefundRequest refundRequest
    ) {
        RefundEntity entity =
                RefundEntity.fromDomain(refundRequest);

        return repository.save(entity).toDomain();
    }
}
