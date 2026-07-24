package com.kata.carrefour.domain.model;
import java.math.BigDecimal;

public record OrderItem(
        String id,
        String productName,
        int quantity,
        BigDecimal unitPrice
) {

    public BigDecimal totalPrice() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}