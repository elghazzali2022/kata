package com.carrefour.kata.infrastructure.persistence;

import com.carrefour.kata.application.port.out.OrderRepository;
import com.carrefour.kata.domain.model.CustomerOrder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaOrderRepository implements OrderRepository {
    private final SpringDataOrderRepository repository;

    public JpaOrderRepository(SpringDataOrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<CustomerOrder> findByCustomerId(String customerId) {
        return repository.findByCustomerId(customerId).stream()
                .map(OrderEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<CustomerOrder> findByIdAndCustomerId(String orderId, String customerId) {
        return repository.findByIdAndCustomerId(orderId, customerId).map(OrderEntity::toDomain);
    }
}

