package com.kata.carrefour.application.port.in;

import com.kata.carrefour.domain.model.RefundRequest;

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
    ) {
    }
}
