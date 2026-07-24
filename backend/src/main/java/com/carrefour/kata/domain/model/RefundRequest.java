package com.carrefour.kata.domain.model;

import java.time.Instant;

public record RefundRequest(
        String id,
        String orderId,
        String itemId,
        String description,
        Evidence evidence,
        RefundStatus status,
        Instant createdAt
) {
    public record Evidence(String fileName, String contentType, long size) {}

    public enum RefundStatus {
        SUBMITTED
    }
}

