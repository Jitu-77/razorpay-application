package com.jitu.razorpay_application.payment_service.gateway;

//import com.jitu.RazorPay.payment.gateway.dto.PaymentRequest;
//import com.jitu.RazorPay.payment.gateway.dto.PaymentResult;

import com.jitu.razorpay_application.payment_service.gateway.dto.PaymentRequest;
import com.jitu.razorpay_application.payment_service.gateway.dto.PaymentResult;

import java.util.UUID;

public interface PaymentAdapter {
    PaymentResult initiate(PaymentRequest paymentRequest );

    PaymentResult capture(UUID paymentId);
}

// to switch between any classes based on condition
// interface for classes
// mapper via router