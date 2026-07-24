package com.carrefour.kata.application.port.in;

import com.carrefour.kata.domain.model.CustomerOrder;

import java.util.List;

public interface GetOrderHistoryUseCase {
    List<CustomerOrder> getForCustomer(String customerId);
}

