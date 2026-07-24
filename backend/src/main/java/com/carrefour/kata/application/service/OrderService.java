package com.carrefour.kata.application.service;

import com.carrefour.kata.application.port.in.GetOrderHistoryUseCase;
import com.carrefour.kata.application.port.out.OrderRepository;
import com.carrefour.kata.domain.model.CustomerOrder;

import java.util.Comparator;
import java.util.List;

public class OrderService implements GetOrderHistoryUseCase {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public List<CustomerOrder> getForCustomer(String customerId) {
        return orderRepository.findByCustomerId(customerId).stream()
                .sorted(Comparator.comparing(CustomerOrder::orderedAt).reversed())
                .toList();
    }
}

