package com.kata.carrefour.infrastructure.persistence;

import com.kata.carrefour.domain.model.RefundRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "refund_requests")
class RefundEntity {

    @Id
    private String id;

    private String orderId;

    private String itemId;

    private String description;

    private String evidenceFileName;

    private String evidenceContentType;

    private long evidenceSize;

    @Enumerated(EnumType.STRING)
    private RefundRequest.RefundStatus status;

    private Instant createdAt;

    protected RefundEntity() {
    }

    static RefundEntity fromDomain(RefundRequest request) {
        RefundEntity entity = new RefundEntity();

        entity.id = request.id();
        entity.orderId = request.orderId();
        entity.itemId = request.itemId();
        entity.description = request.description();
        entity.evidenceFileName =
                request.evidence().fileName();
        entity.evidenceContentType =
                request.evidence().contentType();
        entity.evidenceSize =
                request.evidence().size();
        entity.status = request.status();
        entity.createdAt = request.createdAt();

        return entity;
    }

    RefundRequest toDomain() {
        return new RefundRequest(
                id,
                orderId,
                itemId,
                description,
                new RefundRequest.Evidence(
                        evidenceFileName,
                        evidenceContentType,
                        evidenceSize
                ),
                status,
                createdAt
        );
    }
}
