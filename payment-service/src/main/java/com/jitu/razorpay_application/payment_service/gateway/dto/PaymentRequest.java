package com.jitu.razorpay_application.payment_service.gateway.dto;

//import com.jitu.RazorPay.common.entity.Money;
//import com.jitu.RazorPay.common.enums.PaymentMethod;

import com.jitu.razorpay_application.common_lib.entity.Money;
import com.jitu.razorpay_application.common_lib.enums.PaymentMethod;

import java.util.Map;
import java.util.UUID;

public record PaymentRequest(
        UUID paymentId,
        UUID orderId,
        UUID merchantId,
        Money amount,
        PaymentMethod method,
        Map<String, Object> methodDetails
) {
}
