package com.phaneendra.payment_orchestration.service;

import com.phaneendra.payment_orchestration.dto.PaymentRequest;
import com.phaneendra.payment_orchestration.dto.PaymentResponse;
import com.phaneendra.payment_orchestration.exception.PaymentNotFoundException;
import com.phaneendra.payment_orchestration.model.Payment;
import com.phaneendra.payment_orchestration.model.PaymentStatus;
import com.phaneendra.payment_orchestration.repository.PaymentRepository;
import com.phaneendra.payment_orchestration.routing.RoutingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentOrchestrator {

    private final PaymentRepository paymentRepository;

    private final RoutingService routingService;


    private final RetryService retryService;


    public PaymentResponse processPayment(PaymentRequest request, String idempotencyKey) {


        if (idempotencyKey != null && !idempotencyKey.isEmpty()) {

            var existingPayment = paymentRepository.findByIdempotencyKey(idempotencyKey);

            if (existingPayment.isPresent()) {

                Payment payment = existingPayment.get();

                return PaymentResponse.builder()
                        .paymentId(payment.getExternalId())
                        .status(payment.getStatus().name())
                        .provider(payment.getProvider())
                        .build();
            }
        }


        Payment payment = Payment.builder()
                .externalId(UUID.randomUUID().toString())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .paymentMethod(request.getPaymentMethod())
                .status(PaymentStatus.INITIATED)
                .provider("NA")
                .idempotencyKey(idempotencyKey)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();



        // Step 1: Get primary provider
        var primaryProvider = routingService.route(request.getPaymentMethod());

        // Step 2: Retry logic
        boolean success = retryService.executeWithRetry(primaryProvider, payment);

        // Step 3: Failover if needed
        if (!success) {

            // Switch provider
            String fallbackMethod = request.getPaymentMethod().equalsIgnoreCase("CARD") ? "UPI" : "CARD";

            var fallbackProvider = routingService.route(fallbackMethod);

            success = fallbackProvider.process(payment);

            payment.setProvider(fallbackProvider.getName());

        } else {
            payment.setProvider(primaryProvider.getName());
        }

        // Step 4: Update status
        payment.setStatus(success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED);
        payment.setUpdatedAt(LocalDateTime.now());



        Payment saved = paymentRepository.save(payment);

        return PaymentResponse.builder()
                .paymentId(saved.getExternalId())
                .status(saved.getStatus().name())
                .provider(saved.getProvider())
                .build();
    }


    public PaymentResponse getPayment(String id) {

        Payment payment = paymentRepository.findByExternalId(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));

        return PaymentResponse.builder()
                .paymentId(payment.getExternalId())
                .status(payment.getStatus().name())
                .provider(payment.getProvider())
                .build();
    }
}