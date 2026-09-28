package com.carrefour.kata.api;

import com.carrefour.kata.api.dto.ChangePlanRequest;
import com.carrefour.kata.api.dto.SubscribeRequest;
import com.carrefour.kata.api.dto.SubscriptionResponse;
import com.carrefour.kata.application.SubscriptionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService service;

    public SubscriptionController(SubscriptionService service) {
        this.service = service;
    }

    @PostMapping
    public SubscriptionResponse subscribe(
            @Valid @RequestBody SubscribeRequest request
    ) {
        return SubscriptionResponse.from(
                service.subscribe(request.customerId(), request.plan())
        );
    }

    @GetMapping("/{customerId}")
    public SubscriptionResponse get(
            @PathVariable UUID customerId
    ) {
        return SubscriptionResponse.from(service.get(customerId));
    }

    @PutMapping("/{customerId}/plan")
    public SubscriptionResponse changePlan(
            @PathVariable UUID customerId,
            @Valid @RequestBody ChangePlanRequest request
    ) {
        return SubscriptionResponse.from(
                service.changePlan(customerId, request.plan())
        );
    }
}
