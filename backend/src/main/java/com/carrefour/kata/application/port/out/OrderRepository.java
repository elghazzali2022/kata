package com.carrefour.kata.application.port.out;

import com.carrefour.kata.domain.model.CustomerOrder;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    List<CustomerOrder> findByCustomerId(String customerId);
    Optional<CustomerOrder> findByIdAndCustomerId(String orderId, String customerId);
}

