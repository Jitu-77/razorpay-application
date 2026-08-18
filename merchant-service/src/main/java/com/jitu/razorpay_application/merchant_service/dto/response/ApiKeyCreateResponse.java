package com.jitu.razorpay_application.merchant_service.dto.response;

import com.jitu.razorpay_application.common_lib.enums.Environment;

import java.util.UUID;

public record ApiKeyCreateResponse(
        UUID id,
        String keyId,
        String keySecret,
        Environment environment
) {
}
