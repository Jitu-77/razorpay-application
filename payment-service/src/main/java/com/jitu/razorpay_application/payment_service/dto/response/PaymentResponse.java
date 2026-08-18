package com.jitu.razorpay_application.payment_service.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.jitu.razorpay_application.common_lib.entity.Money;
import com.jitu.razorpay_application.common_lib.enums.PaymentMethod;
import com.jitu.razorpay_application.common_lib.enums.PaymentStatus;

//import com.jitu.RazorPay.common.entity.Money;
//import com.jitu.RazorPay.common.enums.PaymentMethod;
//import com.jitu.RazorPay.common.enums.PaymentStatus;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PaymentResponse (
        UUID id,
        UUID orderId,
        UUID merchantId,
        Money amount,
        PaymentStatus status,
        PaymentMethod method,
        Map<String,Object> methodDetails,
        String errorCode,
        String errorDescription,
        LocalDateTime capturedAt,
        LocalDateTime createdAt
){

}
