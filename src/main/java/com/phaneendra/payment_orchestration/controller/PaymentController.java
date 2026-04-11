package com.phaneendra.payment_orchestration.controller;

import com.phaneendra.payment_orchestration.dto.PaymentRequest;
import com.phaneendra.payment_orchestration.dto.PaymentResponse;
import com.phaneendra.payment_orchestration.service.PaymentOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentOrchestrator orchestrator;

    @PostMapping
    public PaymentResponse createPayment(
            @RequestHeader(value = "Idempotency-Key", required = false) String key,
            @RequestBody PaymentRequest request) {

        return orchestrator.processPayment(request, key);
    }

    @GetMapping("/{id}")
    public PaymentResponse getPayment(@PathVariable String id) {
        return orchestrator.getPayment(id);
    }
}
