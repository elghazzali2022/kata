package com.kata.carrefour.application.port.out;

import com.kata.carrefour.domain.model.RefundRequest;

public interface RefundRepository {

    boolean existsByOrderIdAndItemId(
            String orderId,
            String itemId
    );

    RefundRequest save(RefundRequest refundRequest);
}