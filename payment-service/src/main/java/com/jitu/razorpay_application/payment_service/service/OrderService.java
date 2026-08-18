package com.jitu.razorpay_application.payment_service.service;

//import com.jitu.RazorPay.payment.dto.request.CreateOrderRequest;
//import com.jitu.RazorPay.payment.dto.response.OrderResponse;
//import com.jitu.RazorPay.payment.dto.response.PaymentResponse;

import com.jitu.razorpay_application.payment_service.dto.request.CreateOrderRequest;
import com.jitu.razorpay_application.payment_service.dto.response.OrderResponse;
import com.jitu.razorpay_application.payment_service.dto.response.PaymentResponse;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderResponse create(UUID merchantId, CreateOrderRequest request);
    OrderResponse getById(UUID merchantId,UUID orderId);
    OrderResponse cancel(UUID merchantId,UUID orderId);
    List<PaymentResponse> listPayment(UUID merchantId, UUID orderId);
}
