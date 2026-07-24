package com.kata.carrefour.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

interface SpringDataOrderRepository
        extends JpaRepository<OrderEntity, String> {

    List<OrderEntity> findByCustomerId(String customerId);

    Optional<OrderEntity> findByIdAndCustomerId(
            String id,
            String customerId
    );
}
