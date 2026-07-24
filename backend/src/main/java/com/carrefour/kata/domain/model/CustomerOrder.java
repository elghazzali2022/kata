package com.carrefour.kata.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public record CustomerOrder(
        String id,
        String customerId,
        LocalDate orderedAt,
        LocalDate deliveredAt,
        OrderStatus status,
        List<OrderItem> items
) {
    public CustomerOrder {
        items = List.copyOf(items);
    }

    public Optional<OrderItem> findItem(String itemId) {
        return items.stream().filter(item -> item.id().equals(itemId)).findFirst();
    }

    public BigDecimal totalPrice() {
        return items.stream()
                .map(OrderItem::totalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

