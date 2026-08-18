package com.jitu.razorpay_application.payment_service.service;

//import com.jitu.RazorPay.payment.dto.request.PaymentInitRequest;
//import com.jitu.RazorPay.payment.dto.response.PaymentResponse;

import com.jitu.razorpay_application.payment_service.dto.request.PaymentInitRequest;
import com.jitu.razorpay_application.payment_service.dto.response.PaymentResponse;

import java.util.UUID;

public interface PaymentService {

PaymentResponse intitiate(UUID merchantId, PaymentInitRequest paymentInitRequest);

PaymentResponse capture(UUID merchantId, UUID paymentId);
void resolveAuthorization(UUID paymentId, boolean approve, String bankRef, String errorCode, String errorDescription);
}
