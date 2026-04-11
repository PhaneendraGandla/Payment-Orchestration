package com.phaneendra.payment_orchestration.service;


import com.phaneendra.payment_orchestration.dto.PaymentRequest;
import com.phaneendra.payment_orchestration.repository.PaymentRepository;
import com.phaneendra.payment_orchestration.routing.RoutingService;
import com.phaneendra.payment_orchestration.provider.PaymentProvider;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class PaymentOrchestratorTest {

    @Test
    void testPaymentSuccess() {

        // Step 1: Create fake dependencies
        PaymentRepository repo = mock(PaymentRepository.class);
        RoutingService routing = mock(RoutingService.class);
        RetryService retry = mock(RetryService.class);
        PaymentProvider provider = mock(PaymentProvider.class);

        // Step 2: Create orchestrator
        PaymentOrchestrator orchestrator =
                new PaymentOrchestrator(repo, routing, retry);

        // Step 3: Prepare request
        PaymentRequest request = new PaymentRequest();
        request.setAmount(BigDecimal.valueOf(1000));
        request.setCurrency("INR");
        request.setPaymentMethod("CARD");

        // Step 4: Define fake behavior
        when(routing.route("CARD")).thenReturn(provider);
        when(provider.process(any())).thenReturn(true);
        when(provider.getName()).thenReturn("ProviderA");
        when(repo.save(any())).thenAnswer(i -> i.getArguments()[0]);

        // Step 5: Call method
        var response = orchestrator.processPayment(request, "test-key");

        // Step 6: Verify result
        assertEquals("SUCCESS", response.getStatus());
        assertEquals("ProviderA", response.getProvider());
    }



    @Test
    void testIdempotency() {

        PaymentRepository repo = mock(PaymentRepository.class);
        RoutingService routing = mock(RoutingService.class);
        RetryService retry = mock(RetryService.class);

        PaymentOrchestrator orchestrator =
                new PaymentOrchestrator(repo, routing, retry);

        var existing = com.phaneendra.payment_orchestration.model.Payment.builder()
                .externalId("123")
                .status(com.phaneendra.payment_orchestration.model.PaymentStatus.SUCCESS)
                .provider("ProviderA")
                .idempotencyKey("same-key")
                .build();

        when(repo.findByIdempotencyKey("same-key"))
                .thenReturn(java.util.Optional.of(existing));

        PaymentRequest request = new PaymentRequest();
        request.setAmount(BigDecimal.valueOf(1000));
        request.setCurrency("INR");
        request.setPaymentMethod("CARD");

        var response = orchestrator.processPayment(request, "same-key");

        assertEquals("123", response.getPaymentId());
    }
}
