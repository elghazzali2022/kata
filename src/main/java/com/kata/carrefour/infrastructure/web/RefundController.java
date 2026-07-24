package com.kata.carrefour.infrastructure.web;

import com.kata.carrefour.application.port.in.SubmitRefundUseCase;
import com.kata.carrefour.domain.model.RefundRequest;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/refunds")
public class RefundController {

    private final SubmitRefundUseCase submitRefund;

    public RefundController(
            SubmitRefundUseCase submitRefund
    ) {
        this.submitRefund = submitRefund;
    }

    @PostMapping(consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    public RefundRequest submit(
            @RequestHeader(
                    value = "X-Customer-Id",
                    defaultValue = "customer-1"
            )
            String customerId,

            @RequestParam
            @NotBlank
            String orderId,

            @RequestParam
            @NotBlank
            String itemId,

            @RequestParam
            @NotBlank
            String description,

            @RequestParam
            MultipartFile evidence
    ) {
        SubmitRefundUseCase.SubmitRefundCommand command =
                new SubmitRefundUseCase.SubmitRefundCommand(
                        customerId,
                        orderId,
                        itemId,
                        description,
                        evidence.getOriginalFilename(),
                        evidence.getContentType(),
                        evidence.getSize()
                );

        return submitRefund.submit(command);
    }
}