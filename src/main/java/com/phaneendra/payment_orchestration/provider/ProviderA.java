package com.phaneendra.payment_orchestration.provider;

import com.phaneendra.payment_orchestration.model.Payment;
import org.springframework.stereotype.Component;

import java.util.Random;


// Card
@Component
public class ProviderA implements PaymentProvider {

    private final Random random = new Random();

    @Override
    public String getName() {
        return "ProviderA";
    }

    @Override
    public boolean process(Payment payment) {

        // Simulate success/failure
        return random.nextBoolean(); // 50% success
    }
}
