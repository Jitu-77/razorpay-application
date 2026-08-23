package com.jitu.razorpay_application.common_lib.dto;

import com.jitu.razorpay_application.common_lib.entity.Money;

import java.util.Map;
import java.util.UUID;

public record VaultChargeRequest(
        UUID paymentId,
        String token,
        Money amount,
        Map<String, Object> methodDetails
) {
}
