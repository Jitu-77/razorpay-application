package com.jitu.razorpay_application.payment_service.api;

//import com.jitu.RazorPay.payment.entity.Payment;

import com.jitu.razorpay_application.payment_service.entity.Payment;

import java.util.List;
import java.util.UUID;

public interface PaymentLookupService {
    List<Payment> findUnsettledCapturedPayments(UUID merchantId);
}
