package com.carrefour.kata.infrastructure.persistence;

import com.carrefour.kata.domain.model.CustomerOrder;
import com.carrefour.kata.domain.model.OrderItem;
import com.carrefour.kata.domain.model.OrderStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customer_orders")
class OrderEntity {
    @Id
    private String id;
    private String customerId;
    private LocalDate orderedAt;
    private LocalDate deliveredAt;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderItemEntity> items = new ArrayList<>();

    protected OrderEntity() {
    }

    static OrderEntity fromDomain(CustomerOrder order) {
        OrderEntity entity = new OrderEntity();
        entity.id = order.id();
        entity.customerId = order.customerId();
        entity.orderedAt = order.orderedAt();
        entity.deliveredAt = order.deliveredAt();
        entity.status = order.status();
        order.items().forEach(item -> entity.items.add(OrderItemEntity.fromDomain(item, entity)));
        return entity;
    }

    CustomerOrder toDomain() {
        return new CustomerOrder(
                id,
                customerId,
                orderedAt,
                deliveredAt,
                status,
                items.stream().map(OrderItemEntity::toDomain).toList()
        );
    }
}

@Entity
@Table(name = "order_items")
class OrderItemEntity {
    @Id
    private String id;
    private String productName;
    private int quantity;
    private BigDecimal unitPrice;

    @jakarta.persistence.ManyToOne(fetch = FetchType.LAZY, optional = false)
    @jakarta.persistence.JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    protected OrderItemEntity() {
    }

    static OrderItemEntity fromDomain(OrderItem item, OrderEntity order) {
        OrderItemEntity entity = new OrderItemEntity();
        entity.id = item.id();
        entity.productName = item.productName();
        entity.quantity = item.quantity();
        entity.unitPrice = item.unitPrice();
        entity.order = order;
        return entity;
    }

    OrderItem toDomain() {
        return new OrderItem(id, productName, quantity, unitPrice);
    }
}

