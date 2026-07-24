package com.carrefour.kata.infrastructure.persistence;

import com.carrefour.kata.application.port.out.RefundRepository;
import com.carrefour.kata.domain.model.RefundRequest;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryRefundRepository implements RefundRepository {
    private final Map<String, RefundRequest> requests = new ConcurrentHashMap<>();

    @Override
    public boolean existsByOrderIdAndItemId(String orderId, String itemId) {
        return requests.values().stream()
                .anyMatch(request -> request.orderId().equals(orderId) && request.itemId().equals(itemId));
    }

    @Override
    public RefundRequest save(RefundRequest refundRequest) {
        requests.put(refundRequest.id(), refundRequest);
        return refundRequest;
    }
}

