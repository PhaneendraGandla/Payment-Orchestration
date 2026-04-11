package com.phaneendra.payment_orchestration.service;

import com.phaneendra.payment_orchestration.model.Payment;
import com.phaneendra.payment_orchestration.provider.PaymentProvider;
import org.springframework.stereotype.Service;

@Service
public class RetryService {

    private static final int MAX_RETRIES = 2;

    public boolean executeWithRetry(PaymentProvider provider, Payment payment) {

        int attempts = 0;

        while (attempts <= MAX_RETRIES) {
            boolean success = provider.process(payment);

            if (success) {
                return true;
            }

            attempts++;
        }

        return false;
    }
}