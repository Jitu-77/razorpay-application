package com.jitu.razorpay_application.vault_service.controllers;

//import com.jitu.RazorPay.merchant.security.MerchantContext;
//import com.jitu.RazorPay.vault.dto.request.TokenizeRequest;
//import com.jitu.RazorPay.vault.dto.response.TokenizeResponse;
//import com.jitu.RazorPay.vault.services.VaultService;


import com.jitu.razorpay_application.common_lib.context.MerchantContext;
import com.jitu.razorpay_application.vault_service.dto.request.TokenizeRequest;
import com.jitu.razorpay_application.vault_service.dto.response.TokenizeResponse;
import com.jitu.razorpay_application.vault_service.services.VaultService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/vault")
public class VaultController {
    private final VaultService vaultService;
    private final MerchantContext merchantContext;
//UUID merchantId = UUID.fromString("e81e9b55-3bc4-4e95-b79d-0288df06265f"); //TODO: replace it with MerchantContext
@PostMapping("/tokenize")
public ResponseEntity<TokenizeResponse> tokenize(@Valid @RequestBody TokenizeRequest tokenizeRequest){
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(vaultService.tokenize(tokenizeRequest,merchantContext.getMerchantId()));
}
}
