package com.jitu.razorpay_application.payment_service.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
//import com.jitu.RazorPay.common.enums.PaymentMethod;
import com.jitu.razorpay_application.common_lib.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PaymentInitRequest(
        @NotNull(message = "Order Id is required")
        UUID orderId,

        @NotNull(message = "Payment method is required")
        PaymentMethod method,

        Map<String, Object> methodDetails
) {

}
