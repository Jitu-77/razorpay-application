package com.jitu.razorpay_application.payment_service.processor;

//import com.jitu.RazorPay.payment.processor.dto.PaymentProcessorRequest;
//import com.jitu.RazorPay.payment.processor.dto.PaymentProcessorResponse;

import com.jitu.razorpay_application.payment_service.processor.dto.PaymentProcessorRequest;
import com.jitu.razorpay_application.payment_service.processor.dto.PaymentProcessorResponse;

public interface PaymentProcessor {
    PaymentProcessorResponse charge(PaymentProcessorRequest paymentProcessorRequest);
}
