package com.phaneendra.payment_orchestration.provider;

import com.phaneendra.payment_orchestration.model.Payment;

public interface PaymentProvider {

    String getName();

    boolean process(Payment payment);
}