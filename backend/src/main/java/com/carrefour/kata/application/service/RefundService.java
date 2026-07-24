package com.carrefour.kata.application.service;

import com.carrefour.kata.application.port.in.SubmitRefundUseCase;
import com.carrefour.kata.application.port.out.OrderRepository;
import com.carrefour.kata.application.port.out.RefundRepository;
import com.carrefour.kata.domain.exception.BusinessRuleException;
import com.carrefour.kata.domain.model.CustomerOrder;
import com.carrefour.kata.domain.model.OrderStatus;
import com.carrefour.kata.domain.model.RefundRequest;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public class RefundService implements SubmitRefundUseCase {
    static final int MAX_DESCRIPTION_LENGTH = 500;
    static final long MAX_EVIDENCE_SIZE = 5 * 1024 * 1024;
    static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    private final OrderRepository orderRepository;
    private final RefundRepository refundRepository;
    private final Clock clock;

    public RefundService(OrderRepository orderRepository, RefundRepository refundRepository, Clock clock) {
        this.orderRepository = orderRepository;
        this.refundRepository = refundRepository;
        this.clock = clock;
    }

    @Override
    public RefundRequest submit(SubmitRefundCommand command) {
        validateDescription(command.description());
        validateEvidence(command.contentType(), command.fileSize());

        CustomerOrder order = orderRepository.findByIdAndCustomerId(command.orderId(), command.customerId())
                .orElseThrow(() -> new BusinessRuleException("Order not found"));

        if (order.status() != OrderStatus.DELIVERED || order.deliveredAt() == null) {
            throw new BusinessRuleException("Only delivered orders can be refunded");
        }
        LocalDate today = LocalDate.now(clock);
        if (order.deliveredAt().isBefore(today.minusDays(30))) {
            throw new BusinessRuleException("The 30-day refund period has expired");
        }
        order.findItem(command.itemId())
                .orElseThrow(() -> new BusinessRuleException("Product not found in this order"));
        if (refundRepository.existsByOrderIdAndItemId(command.orderId(), command.itemId())) {
            throw new BusinessRuleException("A refund request already exists for this product");
        }

        RefundRequest request = new RefundRequest(
                UUID.randomUUID().toString(),
                command.orderId(),
                command.itemId(),
                command.description().trim(),
                new RefundRequest.Evidence(command.fileName(), command.contentType(), command.fileSize()),
                RefundRequest.RefundStatus.SUBMITTED,
                Instant.now(clock)
        );
        return refundRepository.save(request);
    }

    private void validateDescription(String description) {
        if (description == null || description.trim().length() < 10) {
            throw new BusinessRuleException("Description must contain at least 10 characters");
        }
        if (description.trim().length() > MAX_DESCRIPTION_LENGTH) {
            throw new BusinessRuleException("Description must not exceed 500 characters");
        }
    }

    private void validateEvidence(String contentType, long size) {
        if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new BusinessRuleException("Evidence must be a JPEG, PNG or WebP image");
        }
        if (size <= 0 || size > MAX_EVIDENCE_SIZE) {
            throw new BusinessRuleException("Evidence must not be empty or exceed 5 MB");
        }
    }
}

