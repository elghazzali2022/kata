package com.kata.carrefour.application.port.out;

import com.kata.carrefour.domain.model.CustomerOrder;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    List<CustomerOrder> findByCustomerId(String customerId);

    Optional<CustomerOrder> findByIdAndCustomerId(
            String orderId,
            String customerId
    );
}