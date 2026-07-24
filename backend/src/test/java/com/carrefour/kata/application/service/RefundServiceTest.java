package com.carrefour.kata.application.service;

import com.carrefour.kata.application.port.in.SubmitRefundUseCase.SubmitRefundCommand;
import com.carrefour.kata.application.port.out.OrderRepository;
import com.carrefour.kata.application.port.out.RefundRepository;
import com.carrefour.kata.domain.exception.BusinessRuleException;
import com.carrefour.kata.domain.model.CustomerOrder;
import com.carrefour.kata.domain.model.OrderItem;
import com.carrefour.kata.domain.model.OrderStatus;
import com.carrefour.kata.domain.model.RefundRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RefundServiceTest {
    private static final Instant NOW = Instant.parse("2026-07-24T10:00:00Z");
    private RefundService service;
    private FakeRefundRepository refunds;

    @BeforeEach
    void setUp() {
        CustomerOrder order = new CustomerOrder(
                "order-1", "customer-1", LocalDate.of(2026, 7, 10), LocalDate.of(2026, 7, 15),
                OrderStatus.DELIVERED,
                List.of(new OrderItem("item-1", "Cafetière", 1, new BigDecimal("39.90")))
        );
        OrderRepository orders = new FakeOrderRepository(order);
        refunds = new FakeRefundRepository();
        service = new RefundService(orders, refunds, Clock.fixed(NOW, ZoneOffset.UTC));
    }

    @Test
    void submitsARefundForAnEligibleProduct() {
        RefundRequest result = service.submit(validCommand());

        assertThat(result.status()).isEqualTo(RefundRequest.RefundStatus.SUBMITTED);
        assertThat(result.description()).isEqualTo("Produit arrivé cassé");
        assertThat(refunds.saved).isEqualTo(result);
    }

    @Test
    void rejectsAnUnknownProduct() {
        SubmitRefundCommand command = new SubmitRefundCommand(
                "customer-1", "order-1", "unknown", "Produit arrivé cassé", "photo.png", "image/png", 1000
        );

        assertThatThrownBy(() -> service.submit(command))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Product not found in this order");
    }

    @Test
    void rejectsADuplicateRequest() {
        service.submit(validCommand());

        assertThatThrownBy(() -> service.submit(validCommand()))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("A refund request already exists for this product");
    }

    @Test
    void rejectsUnsupportedEvidence() {
        SubmitRefundCommand command = new SubmitRefundCommand(
                "customer-1", "order-1", "item-1", "Produit arrivé cassé", "proof.pdf", "application/pdf", 1000
        );

        assertThatThrownBy(() -> service.submit(command))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("JPEG, PNG or WebP");
    }

    private SubmitRefundCommand validCommand() {
        return new SubmitRefundCommand(
                "customer-1", "order-1", "item-1", "Produit arrivé cassé", "photo.png", "image/png", 1000
        );
    }

    private record FakeOrderRepository(CustomerOrder order) implements OrderRepository {
        @Override
        public List<CustomerOrder> findByCustomerId(String customerId) {
            return List.of(order);
        }

        @Override
        public Optional<CustomerOrder> findByIdAndCustomerId(String orderId, String customerId) {
            return order.id().equals(orderId) && order.customerId().equals(customerId)
                    ? Optional.of(order) : Optional.empty();
        }
    }

    private static final class FakeRefundRepository implements RefundRepository {
        private RefundRequest saved;

        @Override
        public boolean existsByOrderIdAndItemId(String orderId, String itemId) {
            return saved != null && saved.orderId().equals(orderId) && saved.itemId().equals(itemId);
        }

        @Override
        public RefundRequest save(RefundRequest refundRequest) {
            saved = refundRequest;
            return refundRequest;
        }
    }
}
