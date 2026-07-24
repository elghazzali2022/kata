package com.kata.carrefour.application.service;

import com.kata.carrefour.application.port.in.GetOrderHistoryUseCase;
import com.kata.carrefour.application.port.out.OrderRepository;
import com.kata.carrefour.domain.model.CustomerOrder;

import java.util.Comparator;
import java.util.List;

public class OrderService implements GetOrderHistoryUseCase {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public List<CustomerOrder> getForCustomer(String customerId) {
        return orderRepository.findByCustomerId(customerId)
                .stream()
                .sorted(
                        Comparator.comparing(CustomerOrder::orderedAt)
                                .reversed()
                )
                .toList();
    }
}
