package com.jitu.razorpay_application.payment_service.processor.strategy;

//import com.jitu.RazorPay.common.uti.RandomizerUtil;
//import com.jitu.RazorPay.payment.processor.PaymentProcessor;
//import com.jitu.RazorPay.payment.processor.dto.PaymentProcessorRequest;
//import com.jitu.RazorPay.payment.processor.dto.PaymentProcessorResponse;

import com.jitu.razorpay_application.common_lib.uti.RandomizerUtil;
import com.jitu.razorpay_application.payment_service.processor.PaymentProcessor;
import com.jitu.razorpay_application.payment_service.processor.dto.PaymentProcessorRequest;
import com.jitu.razorpay_application.payment_service.processor.dto.PaymentProcessorResponse;
import org.springframework.stereotype.Component;

@Component
public class NetBankingPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest paymentProcessorRequest) {
        final String BANK_CODE_FAIL = "BANK_CODE_FAIL";
        String bankCode = paymentProcessorRequest.methodDetails() != null ?
                paymentProcessorRequest.methodDetails().get("bank").toString() : null;

        // simulation
        if (BANK_CODE_FAIL.equals(bankCode)) {
            return new PaymentProcessorResponse.Failure("BANK_REJECTED",
                    "Banked rejected the transaction registration"
            );
        };

        String processorRef = "NBK_PROCESSOR_"+ RandomizerUtil.randomBase64(16);

        // this is for the client to the respective bank
        //bank ref is given by bank on transaction success to our server
//        String redirectRef = "http://REDIRECT_BANK.com/"+processorRef;

//        return new PaymentProcessorResponse.Success(processorRef,redirectRef);
        return new PaymentProcessorResponse.Pending(processorRef);
    }

}
