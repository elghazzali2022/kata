package com.kata.carrefour.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataRefundRepository
        extends JpaRepository<RefundEntity, String> {

    boolean existsByOrderIdAndItemId(
            String orderId,
            String itemId
    );
}
