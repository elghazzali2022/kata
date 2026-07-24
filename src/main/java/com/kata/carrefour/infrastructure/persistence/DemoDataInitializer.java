package com.kata.carrefour.infrastructure.persistence;

import com.kata.carrefour.domain.model.CustomerOrder;
import com.kata.carrefour.domain.model.OrderItem;
import com.kata.carrefour.domain.model.OrderStatus;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
class DemoDataInitializer implements ApplicationRunner {

    private static final String DEMO_CUSTOMER = "customer-1";

    private final SpringDataOrderRepository orders;

    DemoDataInitializer(
            SpringDataOrderRepository orders
    ) {
        this.orders = orders;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (orders.count() > 0) {
            return;
        }

        LocalDate today = LocalDate.now();

        List<CustomerOrder> demoOrders = List.of(
                new CustomerOrder(
                        "CMD-2026-003",
                        DEMO_CUSTOMER,
                        today.minusDays(8),
                        today.minusDays(4),
                        OrderStatus.DELIVERED,
                        List.of(
                                new OrderItem(
                                        "item-31",
                                        "Cafetière filtre",
                                        1,
                                        new BigDecimal("39.90")
                                ),
                                new OrderItem(
                                        "item-32",
                                        "Café moulu bio 500 g",
                                        2,
                                        new BigDecimal("8.50")
                                )
                        )
                ),
                new CustomerOrder(
                        "CMD-2026-002",
                        DEMO_CUSTOMER,
                        today.minusDays(18),
                        today.minusDays(13),
                        OrderStatus.DELIVERED,
                        List.of(
                                new OrderItem(
                                        "item-21",
                                        "Mixeur plongeant",
                                        1,
                                        new BigDecimal("54.99")
                                )
                        )
                ),
                new CustomerOrder(
                        "CMD-2026-001",
                        DEMO_CUSTOMER,
                        today.minusDays(45),
                        today.minusDays(40),
                        OrderStatus.DELIVERED,
                        List.of(
                                new OrderItem(
                                        "item-11",
                                        "Grille-pain inox",
                                        1,
                                        new BigDecimal("34.90")
                                )
                        )
                ),
                new CustomerOrder(
                        "CMD-2026-004",
                        DEMO_CUSTOMER,
                        today.minusDays(1),
                        null,
                        OrderStatus.SHIPPED,
                        List.of(
                                new OrderItem(
                                        "item-41",
                                        "Bouilloire électrique",
                                        1,
                                        new BigDecimal("29.90")
                                )
                        )
                )
        );

        orders.saveAll(
                demoOrders.stream()
                        .map(OrderEntity::fromDomain)
                        .toList()
        );
    }
}
