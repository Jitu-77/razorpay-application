package com.jitu.razorpay_application.common_lib.dto;

//import com.jitu.RazorPay.common.entity.Money;
//import com.jitu.RazorPay.common.enums.PaymentMethod;

import com.jitu.razorpay_application.common_lib.entity.Money;
import com.jitu.razorpay_application.common_lib.enums.PaymentMethod;

import java.util.Map;
import java.util.UUID;

public record PaymentProcessorRequest(
        UUID processingId,//any static Id that will be for internal purpose
        UUID paymentId,
        String pan,
        String expiry,
        PaymentMethod method,
        Money amount,
        Map<String, Object> methodDetails
) {
    //method for Card
    public static PaymentProcessorRequest card(UUID paymentId,String pan,String expiry,Money amount,
                                               Map<String, Object> methodDetails){
        return new PaymentProcessorRequest(UUID.randomUUID(),paymentId,pan,
                expiry ,PaymentMethod.CARD,amount,methodDetails);
    }

    // method for non card
    public static PaymentProcessorRequest nonCard(UUID paymentId,Money amount,
                                                  PaymentMethod method,Map<String, Object> methodDetails){
        return new PaymentProcessorRequest(UUID.randomUUID(),paymentId,null,
                null ,method,amount,methodDetails);
    }

}
