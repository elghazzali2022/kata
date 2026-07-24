package com.kata.carrefour.application.port.in;

import com.kata.carrefour.domain.model.CustomerOrder;

import java.util.List;

public interface GetOrderHistoryUseCase {

    List<CustomerOrder> getForCustomer(String customerId);
}
