package com.jitu.razorpay_application.merchant_service.service;

import com.jitu.razorpay_application.merchant_service.dto.request.CreateApiKeyRequest;
import com.jitu.razorpay_application.merchant_service.dto.response.ApiKeyCreateResponse;
import com.jitu.razorpay_application.merchant_service.dto.response.ApiKeyResponse;
import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public interface ApiKeyService {

    ApiKeyCreateResponse create(UUID merchantId, @Valid CreateApiKeyRequest request);
    List<ApiKeyResponse> listByMerchant(UUID merchantId);
    void revoke(UUID merchantId,UUID keyId);
    @Nullable ApiKeyCreateResponse rotate(UUID merchantId, UUID keyId);
}
