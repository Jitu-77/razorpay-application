package com.jitu.razorpay_application.payment_service.controller;

//import com.jitu.RazorPay.merchant.security.MerchantContext;
//import com.jitu.RazorPay.payment.dto.request.PaymentInitRequest;
//import com.jitu.RazorPay.payment.dto.response.PaymentResponse;
//import com.jitu.RazorPay.payment.service.PaymentService;


import com.jitu.razorpay_application.common_lib.context.MerchantContext;
import com.jitu.razorpay_application.payment_service.dto.request.PaymentInitRequest;
import com.jitu.razorpay_application.payment_service.dto.response.PaymentResponse;
import com.jitu.razorpay_application.payment_service.service.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final MerchantContext merchantContext;
//    UUID merchantId = UUID.fromString("e81e9b55-3bc4-4e95-b79d-0288df06265f"); //TODO: replace it with MerchantContext
    @PostMapping
    public ResponseEntity<PaymentResponse> initiatePayment(@Valid @RequestBody PaymentInitRequest paymentInitRequest){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(paymentService.intitiate(merchantContext.getMerchantId(),paymentInitRequest));
    }


    @PostMapping("/{paymentId}/capture")
    public ResponseEntity<PaymentResponse> capture(@PathVariable UUID paymentId){
        return ResponseEntity.ok(
                paymentService.capture(merchantContext.getMerchantId(),paymentId)
        );
    }
}
