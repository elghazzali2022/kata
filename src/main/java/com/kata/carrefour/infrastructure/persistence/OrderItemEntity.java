package com.kata.carrefour.infrastructure.persistence;

import com.kata.carrefour.domain.model.OrderItem;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
class OrderItemEntity {

    @Id
    private String id;

    private String productName;

    private int quantity;

    private BigDecimal unitPrice;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    protected OrderItemEntity() {
    }

    static OrderItemEntity fromDomain(
            OrderItem item,
            OrderEntity order
    ) {
        OrderItemEntity entity = new OrderItemEntity();

        entity.id = item.id();
        entity.productName = item.productName();
        entity.quantity = item.quantity();
        entity.unitPrice = item.unitPrice();
        entity.order = order;

        return entity;
    }

    OrderItem toDomain() {
        return new OrderItem(
                id,
                productName,
                quantity,
                unitPrice
        );
    }
}
