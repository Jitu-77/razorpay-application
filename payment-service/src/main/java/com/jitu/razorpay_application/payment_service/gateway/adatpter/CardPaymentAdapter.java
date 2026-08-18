package com.jitu.razorpay_application.payment_service.gateway.adatpter;

//import com.jitu.RazorPay.payment.gateway.PaymentAdapter;
//import com.jitu.RazorPay.payment.gateway.dto.PaymentRequest;
//import com.jitu.RazorPay.payment.gateway.dto.PaymentResult;
//import com.jitu.RazorPay.payment.processor.dto.PaymentProcessorResponse;
//import com.jitu.RazorPay.vault.services.VaultService;

import com.jitu.razorpay_application.payment_service.gateway.PaymentAdapter;
import com.jitu.razorpay_application.payment_service.gateway.dto.PaymentRequest;
import com.jitu.razorpay_application.payment_service.gateway.dto.PaymentResult;
import com.jitu.razorpay_application.payment_service.processor.dto.PaymentProcessorResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;
@Component
@Slf4j
@RequiredArgsConstructor
public class CardPaymentAdapter implements PaymentAdapter {

    private final VaultService vaultService;

    @Override
    public PaymentResult initiate(PaymentRequest paymentRequest) {
        String token = (String) paymentRequest.methodDetails().get("token");
                PaymentProcessorResponse response = vaultService.charge(
                paymentRequest.paymentId(), token, paymentRequest.amount(), paymentRequest.methodDetails()
        );
        return switch (response) {
            case PaymentProcessorResponse.Success success -> new PaymentResult.Success(success.bankReference());
            case PaymentProcessorResponse.Failure failure -> new PaymentResult.Failure(failure.errorCode(), failure.errorDescription());
            case PaymentProcessorResponse.Pending pending -> new PaymentResult.Pending(pending.processorReference());
        };
    }

    @Override
    public PaymentResult capture(UUID paymentId) {
        return new PaymentResult.Success("CARD_REF");
    }
}
