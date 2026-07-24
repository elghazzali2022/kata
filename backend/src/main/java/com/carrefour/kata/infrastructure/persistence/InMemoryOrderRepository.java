package com.carrefour.kata.infrastructure.persistence;

import com.carrefour.kata.application.port.out.OrderRepository;
import com.carrefour.kata.domain.model.CustomerOrder;
import com.carrefour.kata.domain.model.OrderItem;
import com.carrefour.kata.domain.model.OrderStatus;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class InMemoryOrderRepository implements OrderRepository {
    private static final String DEMO_CUSTOMER = "customer-1";
    private final List<CustomerOrder> orders;

    public InMemoryOrderRepository() {
        LocalDate today = LocalDate.now();
        orders = List.of(
                new CustomerOrder("CMD-2026-003", DEMO_CUSTOMER, today.minusDays(8), today.minusDays(4),
                        OrderStatus.DELIVERED, List.of(
                        new OrderItem("item-31", "Cafetière filtre", 1, new BigDecimal("39.90")),
                        new OrderItem("item-32", "Café moulu bio 500 g", 2, new BigDecimal("8.50"))
                )),
                new CustomerOrder("CMD-2026-002", DEMO_CUSTOMER, today.minusDays(18), today.minusDays(13),
                        OrderStatus.DELIVERED, List.of(
                        new OrderItem("item-21", "Mixeur plongeant", 1, new BigDecimal("54.99"))
                )),
                new CustomerOrder("CMD-2026-001", DEMO_CUSTOMER, today.minusDays(45), today.minusDays(40),
                        OrderStatus.DELIVERED, List.of(
                        new OrderItem("item-11", "Grille-pain inox", 1, new BigDecimal("34.90"))
                )),
                new CustomerOrder("CMD-2026-004", DEMO_CUSTOMER, today.minusDays(1), null,
                        OrderStatus.SHIPPED, List.of(
                        new OrderItem("item-41", "Bouilloire électrique", 1, new BigDecimal("29.90"))
                ))
        );
    }

    @Override
    public List<CustomerOrder> findByCustomerId(String customerId) {
        return orders.stream().filter(order -> order.customerId().equals(customerId)).toList();
    }

    @Override
    public Optional<CustomerOrder> findByIdAndCustomerId(String orderId, String customerId) {
        return orders.stream()
                .filter(order -> order.id().equals(orderId) && order.customerId().equals(customerId))
                .findFirst();
    }
}

