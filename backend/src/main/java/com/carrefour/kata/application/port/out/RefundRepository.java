package com.carrefour.kata.application.port.out;

import com.carrefour.kata.domain.model.RefundRequest;

public interface RefundRepository {
    boolean existsByOrderIdAndItemId(String orderId, String itemId);
    RefundRequest save(RefundRequest refundRequest);
}

