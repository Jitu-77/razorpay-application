package com.jitu.razorpay_application.payment_service.gateway.dto;

public sealed interface PaymentResult permits PaymentResult.Pending, PaymentResult.Failure,PaymentResult.Success{

    record Pending(String registrationRef) implements PaymentResult{}
    record Success(String bankReference) implements PaymentResult{}
    record Failure(String errorCode, String errorDescription) implements PaymentResult{}

}
