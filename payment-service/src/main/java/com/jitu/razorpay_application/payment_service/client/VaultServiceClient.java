package com.jitu.razorpay_application.payment_service.client;

//import com.codingshuttle.razorpay.common_lib.dto.PaymentProcessorResponse;
//import com.codingshuttle.razorpay.common_lib.dto.VaultChargeRequest;
import com.jitu.razorpay_application.common_lib.dto.PaymentProcessorResponse;
import com.jitu.razorpay_application.common_lib.dto.VaultChargeRequest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "vault-service", path = "/internal/vault")
public interface VaultServiceClient {

    @PostMapping("/charge")
    PaymentProcessorResponse charge(@RequestBody VaultChargeRequest request);
}
