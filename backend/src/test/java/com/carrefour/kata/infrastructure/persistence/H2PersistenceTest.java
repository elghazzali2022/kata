package com.carrefour.kata.infrastructure.persistence;

import com.carrefour.kata.application.port.out.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:refund-test;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class H2PersistenceTest {
    @Autowired
    private OrderRepository orders;

    @Test
    void initializesAndReadsDemoOrdersFromH2() {
        assertThat(orders.findByCustomerId("customer-1"))
                .hasSize(4)
                .allSatisfy(order -> assertThat(order.items()).isNotEmpty());
    }
}
