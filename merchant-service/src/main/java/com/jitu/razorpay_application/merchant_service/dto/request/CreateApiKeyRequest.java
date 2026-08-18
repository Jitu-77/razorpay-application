package com.jitu.razorpay_application.merchant_service.dto.request;

import com.jitu.razorpay_application.common_lib.enums.Environment;

public record CreateApiKeyRequest(
        Environment environment
) {
}
