package com.jitu.razorpay_application.vault_service.services;

//import com.jitu.RazorPay.common.entity.Money;
//import com.jitu.RazorPay.payment.processor.dto.PaymentProcessorResponse;
//import com.jitu.RazorPay.vault.dto.request.TokenizeRequest;
//import com.jitu.RazorPay.vault.dto.response.TokenizeResponse;

import com.jitu.razorpay_application.common_lib.entity.Money;
import com.jitu.razorpay_application.vault_service.dto.request.TokenizeRequest;
import com.jitu.razorpay_application.vault_service.dto.response.TokenizeResponse;

import java.util.Map;
import java.util.UUID;

//@Service
public interface VaultService {
    TokenizeResponse tokenize(TokenizeRequest request, UUID merchantId);
    PaymentProcessorResponse charge(UUID paymentId, String token, Money amount, Map<String, Object> methodDetails);
}
