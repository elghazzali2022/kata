package com.kata.carrefour.infrastructure.config;

import com.kata.carrefour.application.port.in.GetOrderHistoryUseCase;
import com.kata.carrefour.application.port.in.SubmitRefundUseCase;
import com.kata.carrefour.application.port.out.OrderRepository;
import com.kata.carrefour.application.port.out.RefundRepository;
import com.kata.carrefour.application.service.OrderService;
import com.kata.carrefour.application.service.RefundService;
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
    GetOrderHistoryUseCase getOrderHistoryUseCase(
            OrderRepository orderRepository
    ) {
        return new OrderService(orderRepository);
    }

    @Bean
    SubmitRefundUseCase submitRefundUseCase(
            OrderRepository orderRepository,
            RefundRepository refundRepository,
            Clock clock
    ) {
        return new RefundService(
                orderRepository,
                refundRepository,
                clock
        );
    }
}
