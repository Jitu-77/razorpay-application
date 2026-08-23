package com.jitu.razorpay_application.payment_service.processor;

//import com.jitu.RazorPay.common.enums.PaymentMethod;
//import com.jitu.RazorPay.payment.processor.dto.PaymentProcessorRequest;
//import com.jitu.RazorPay.payment.processor.dto.PaymentProcessorResponse;


import com.jitu.razorpay_application.common_lib.dto.PaymentProcessorRequest;
import com.jitu.razorpay_application.common_lib.enums.PaymentMethod;
//import com.jitu.razorpay_application.payment_service.processor.dto.PaymentProcessorResponse;
import com.jitu.razorpay_application.common_lib.dto.PaymentProcessorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
@Component
@RequiredArgsConstructor
public class PaymentProcessorRouter {

    public final Map<PaymentMethod,PaymentProcessor> paymentProcessorMap;
    public PaymentProcessorResponse charge(PaymentProcessorRequest paymentProcessorRequest){
        PaymentProcessor processor = paymentProcessorMap.get(paymentProcessorRequest.method());
        if(processor == null){
            throw new IllegalArgumentException("No payment processor registered for method: "+paymentProcessorRequest.method());
        }
        return processor.charge(paymentProcessorRequest);
    }
}
