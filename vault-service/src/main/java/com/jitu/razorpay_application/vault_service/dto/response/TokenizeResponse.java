package com.jitu.razorpay_application.vault_service.dto.response;

//import com.jitu.RazorPay.common.enums.CardBrand;

import com.jitu.razorpay_application.common_lib.enums.CardBrand;

public record TokenizeResponse(
        String token,
        String lastFour,
        CardBrand brand,
        Integer expiryMonth,
        Integer expiryYear
) {
}
