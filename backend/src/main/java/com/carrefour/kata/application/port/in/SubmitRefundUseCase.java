package com.carrefour.kata.application.port.in;

import com.carrefour.kata.domain.model.RefundRequest;

public interface SubmitRefundUseCase {
    RefundRequest submit(SubmitRefundCommand command);

    record SubmitRefundCommand(
            String customerId,
            String orderId,
            String itemId,
            String description,
            String fileName,
            String contentType,
            long fileSize
    ) {}
}

