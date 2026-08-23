package com.jitu.razorpay_application.payment_service.processor.strategy;

//import com.jitu.RazorPay.common.uti.RandomizerUtil;
//import com.jitu.RazorPay.payment.processor.PaymentProcessor;
//import com.jitu.RazorPay.payment.processor.dto.PaymentProcessorRequest;
//import com.jitu.RazorPay.payment.processor.dto.PaymentProcessorResponse;

import com.jitu.razorpay_application.common_lib.dto.PaymentProcessorRequest;
import com.jitu.razorpay_application.common_lib.dto.PaymentProcessorResponse;
import com.jitu.razorpay_application.common_lib.uti.RandomizerUtil;
import com.jitu.razorpay_application.payment_service.processor.PaymentProcessor;
import org.springframework.stereotype.Component;

@Component
public class UpiPaymentProcessor implements PaymentProcessor {
    @Override

    //FOR UPI the whole transaction sid one via NPCI portal
    // we just ping them and in return they will provide us the details
    public PaymentProcessorResponse charge(PaymentProcessorRequest paymentProcessorRequest) {
        final String VPA_CODE_FAIL = "fail@okaxis";

        String bankCode = paymentProcessorRequest.methodDetails() != null ?
                paymentProcessorRequest.methodDetails().get("vpa").toString() : null;

        // simulation
        if (VPA_CODE_FAIL.equals(bankCode)) {
            return new PaymentProcessorResponse.Failure("UPI_REJECTED",
                    "Banked rejected the transaction registration"
            );
        }

        String processorRef = "UPI_PROCESSOR_"+ RandomizerUtil.randomBase64(16);

        return new PaymentProcessorResponse.Pending(processorRef);

    }
}
