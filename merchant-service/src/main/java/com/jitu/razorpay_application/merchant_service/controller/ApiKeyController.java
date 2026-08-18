package com.jitu.razorpay_application.merchant_service.controller;

import com.jitu.razorpay_application.common_lib.context.MerchantContext;
import com.jitu.razorpay_application.merchant_service.dto.request.CreateApiKeyRequest;
import com.jitu.razorpay_application.merchant_service.dto.response.ApiKeyCreateResponse;
import com.jitu.razorpay_application.merchant_service.dto.response.ApiKeyResponse;
import com.jitu.razorpay_application.merchant_service.service.ApiKeyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
//@RequestMapping("/v1/merchants/{merchantId}/api-keys")
@RequestMapping("/v1/merchants/api-keys")
public class ApiKeyController {
    private final ApiKeyService apiKeyService;
    private final MerchantContext merchantContext;
    @PostMapping
    public ResponseEntity<ApiKeyCreateResponse> create(
//            @PathVariable UUID merchantId,
            @Valid @RequestBody CreateApiKeyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(apiKeyService.create(merchantId, request));
                .body(apiKeyService.create(merchantContext.getMerchantId(), request));
    }

    @GetMapping
    public ResponseEntity<List<ApiKeyResponse>> List(
//            @PathVariable UUID merchantId
    ) {
//        return ResponseEntity.ok(apiKeyService.listByMerchant(merchantId));
        //as we will be sending status as 200 so we are using ok
        return ResponseEntity.ok(apiKeyService.listByMerchant(merchantContext.getMerchantId()));
    }

    @DeleteMapping("/{keyId}")
        public ResponseEntity<Void> revoke(@PathVariable UUID merchantId,@PathVariable UUID keyId){
//        apiKeyService.revoke(merchantId,keyId);
        apiKeyService.revoke(merchantContext.getMerchantId(),keyId);
        return  ResponseEntity.noContent().build(); // this will return nothing
    }

    @PostMapping("/{keyId}/rotate")
    public ResponseEntity<ApiKeyCreateResponse> rotateKey(
//            @PathVariable UUID merchantId,
            @PathVariable UUID keyId){
//        return ResponseEntity.ok( apiKeyService.rotate(merchantId,keyId));
        return ResponseEntity.ok( apiKeyService.rotate(merchantContext.getMerchantId(),keyId));
    }
}
