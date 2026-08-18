package com.jitu.razorpay_application.payment_service.controller;

//import com.jitu.RazorPay.merchant.security.MerchantContext;
//import com.jitu.RazorPay.payment.dto.request.CreateOrderRequest;
//import com.jitu.RazorPay.payment.dto.response.OrderResponse;
//import com.jitu.RazorPay.payment.service.OrderService;


import com.jitu.razorpay_application.common_lib.context.MerchantContext;
import com.jitu.razorpay_application.payment_service.dto.request.CreateOrderRequest;
import com.jitu.razorpay_application.payment_service.dto.response.OrderResponse;
import com.jitu.razorpay_application.payment_service.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final MerchantContext merchantContext;
//    UUID merchantId = UUID.fromString("e81e9b55-3bc4-4e95-b79d-0288df06265f"); //replace with merchant context later
    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody @Valid CreateOrderRequest request){
        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(orderService.create(merchantId,request));
                .body(orderService.create(merchantContext.getMerchantId(),request));
    }
}
