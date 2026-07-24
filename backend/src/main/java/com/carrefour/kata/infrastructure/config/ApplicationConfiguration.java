package com.carrefour.kata.infrastructure.config;

import com.carrefour.kata.application.port.in.GetOrderHistoryUseCase;
import com.carrefour.kata.application.port.in.SubmitRefundUseCase;
import com.carrefour.kata.application.port.out.OrderRepository;
import com.carrefour.kata.application.port.out.RefundRepository;
import com.carrefour.kata.application.service.OrderService;
import com.carrefour.kata.application.service.RefundService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class ApplicationConfiguration {
    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    GetOrderHistoryUseCase getOrderHistoryUseCase(OrderRepository repository) {
        return new OrderService(repository);
    }

    @Bean
    SubmitRefundUseCase submitRefundUseCase(
            OrderRepository orderRepository,
            RefundRepository refundRepository,
            Clock clock
    ) {
        return new RefundService(orderRepository, refundRepository, clock);
    }
}

