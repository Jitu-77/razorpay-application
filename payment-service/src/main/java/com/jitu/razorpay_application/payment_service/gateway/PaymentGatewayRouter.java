package com.jitu.razorpay_application.payment_service.gateway;

//import com.jitu.RazorPay.common.enums.PaymentMethod;
//import com.jitu.RazorPay.payment.gateway.dto.PaymentRequest;
//import com.jitu.RazorPay.payment.gateway.dto.PaymentResult;
import com.jitu.razorpay_application.common_lib.enums.PaymentMethod;
import com.jitu.razorpay_application.payment_service.gateway.dto.PaymentRequest;
import com.jitu.razorpay_application.payment_service.gateway.dto.PaymentResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentGatewayRouter {

    private final Map<PaymentMethod,PaymentAdapter> paymentAdapter;
    public PaymentResult initiate(PaymentRequest paymentRequest){
    PaymentAdapter paymentAdapterMethod = paymentAdapter.get(paymentRequest.method());
    if(paymentAdapterMethod == null){
        throw new IllegalArgumentException("No payment adapter registered for method: "+paymentRequest.method());
    }
    return paymentAdapterMethod.initiate(paymentRequest);
    }

    public PaymentResult capture(PaymentMethod paymentMethod, UUID paymentId){
        PaymentAdapter adapter = paymentAdapter.get(paymentMethod);
        if(adapter == null){
            throw new IllegalArgumentException("No payment adapter registered for method: "+paymentMethod);
        }
        return adapter.capture(paymentId);
    }
}
