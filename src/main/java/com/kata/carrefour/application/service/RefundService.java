package com.kata.carrefour.application.service;

import com.kata.carrefour.application.port.in.SubmitRefundUseCase;
import com.kata.carrefour.application.port.out.OrderRepository;
import com.kata.carrefour.application.port.out.RefundRepository;
import com.kata.carrefour.domain.exception.BusinessRuleException;
import com.kata.carrefour.domain.model.CustomerOrder;
import com.kata.carrefour.domain.model.OrderStatus;
import com.kata.carrefour.domain.model.RefundRequest;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public class RefundService implements SubmitRefundUseCase {

    private static final int MAX_DESCRIPTION_LENGTH = 500;
    private static final long MAX_EVIDENCE_SIZE = 5 * 1024 * 1024;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    private final OrderRepository orderRepository;
    private final RefundRepository refundRepository;
    private final Clock clock;

    public RefundService(
            OrderRepository orderRepository,
            RefundRepository refundRepository,
            Clock clock
    ) {
        this.orderRepository = orderRepository;
        this.refundRepository = refundRepository;
        this.clock = clock;
    }

    @Override
    public RefundRequest submit(SubmitRefundCommand command) {
        validateDescription(command.description());
        validateEvidence(command.contentType(), command.fileSize());

        CustomerOrder order = orderRepository
                .findByIdAndCustomerId(
                        command.orderId(),
                        command.customerId()
                )
                .orElseThrow(
                        () -> new BusinessRuleException(
                                "Commande introuvable"
                        )
                );

        if (order.status() != OrderStatus.DELIVERED
                || order.deliveredAt() == null) {
            throw new BusinessRuleException(
                    "Seules les commandes livrées peuvent être remboursées"
            );
        }

        LocalDate today = LocalDate.now(clock);

        if (order.deliveredAt().isBefore(today.minusDays(30))) {
            throw new BusinessRuleException(
                    "Le délai de remboursement de 30 jours est dépassé"
            );
        }

        order.findItem(command.itemId())
                .orElseThrow(
                        () -> new BusinessRuleException(
                                "Le produit n'appartient pas à cette commande"
                        )
                );

        boolean alreadyExists =
                refundRepository.existsByOrderIdAndItemId(
                        command.orderId(),
                        command.itemId()
                );

        if (alreadyExists) {
            throw new BusinessRuleException(
                    "Une demande existe déjà pour ce produit"
            );
        }

        RefundRequest request = new RefundRequest(
                UUID.randomUUID().toString(),
                command.orderId(),
                command.itemId(),
                command.description().trim(),
                new RefundRequest.Evidence(
                        command.fileName(),
                        command.contentType(),
                        command.fileSize()
                ),
                RefundRequest.RefundStatus.SUBMITTED,
                Instant.now(clock)
        );

        return refundRepository.save(request);
    }

    private void validateDescription(String description) {
        if (description == null
                || description.trim().length() < 10) {
            throw new BusinessRuleException(
                    "La description doit contenir au moins 10 caractères"
            );
        }

        if (description.trim().length() > MAX_DESCRIPTION_LENGTH) {
            throw new BusinessRuleException(
                    "La description ne doit pas dépasser 500 caractères"
            );
        }
    }

    private void validateEvidence(
            String contentType,
            long fileSize
    ) {
        if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new BusinessRuleException(
                    "La preuve doit être une image JPEG, PNG ou WebP"
            );
        }

        if (fileSize <= 0 || fileSize > MAX_EVIDENCE_SIZE) {
            throw new BusinessRuleException(
                    "La preuve ne doit pas être vide ou dépasser 5 Mo"
            );
        }
    }
}
