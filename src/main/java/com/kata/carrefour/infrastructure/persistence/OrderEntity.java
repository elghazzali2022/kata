package com.kata.carrefour.infrastructure.persistence;

import com.kata.carrefour.domain.model.CustomerOrder;
import com.kata.carrefour.domain.model.OrderStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

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

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
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

        order.items().forEach(item ->
                entity.items.add(
                        OrderItemEntity.fromDomain(item, entity)
                )
        );

        return entity;
    }

    CustomerOrder toDomain() {
        return new CustomerOrder(
                id,
                customerId,
                orderedAt,
                deliveredAt,
                status,
                items.stream()
                        .map(OrderItemEntity::toDomain)
                        .toList()
        );
    }
}
