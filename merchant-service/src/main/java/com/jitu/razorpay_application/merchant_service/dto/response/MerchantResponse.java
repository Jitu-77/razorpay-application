package com.jitu.razorpay_application.merchant_service.dto.response;

import com.jitu.razorpay_application.common_lib.enums.BusinessType;
import com.jitu.razorpay_application.common_lib.enums.MerchantStatus;

import java.util.UUID;

public record MerchantResponse (
        UUID id,
        String name,
        String email,
        String businessName,
        BusinessType businessType,
        MerchantStatus merchantStatus
){
}
