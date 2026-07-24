package com.carrefour.kata.infrastructure.web;

import com.carrefour.kata.application.port.in.GetOrderHistoryUseCase;
import com.carrefour.kata.domain.model.CustomerOrder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final GetOrderHistoryUseCase getOrderHistory;

    public OrderController(GetOrderHistoryUseCase getOrderHistory) {
        this.getOrderHistory = getOrderHistory;
    }

    @GetMapping
    public List<CustomerOrder> history(
            @RequestHeader(value = "X-Customer-Id", defaultValue = "customer-1") String customerId
    ) {
        return getOrderHistory.getForCustomer(customerId);
    }
}

