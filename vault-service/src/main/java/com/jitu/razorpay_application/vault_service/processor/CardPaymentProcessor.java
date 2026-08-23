package com.jitu.razorpay_application.vault_service.processor;

//import com.jitu.RazorPay.common.uti.RandomizerUtil;
//import com.jitu.RazorPay.payment.processor.PaymentProcessor;
//import com.jitu.RazorPay.payment.processor.dto.PaymentProcessorRequest;
//import com.jitu.RazorPay.payment.processor.dto.PaymentProcessorResponse;


import com.jitu.razorpay_application.common_lib.dto.PaymentProcessorRequest;
import com.jitu.razorpay_application.common_lib.dto.PaymentProcessorResponse;
import com.jitu.razorpay_application.common_lib.uti.RandomizerUtil;
//import com.jitu.razorpay_application.payment_service.processor.PaymentProcessor;
//import com.jitu.razorpay_application.payment_service.processor.dto.PaymentProcessorRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CardPaymentProcessor{
    public static final String PAN_CARD_DECLINED = "4000000000000002";
    public static final String PAN_CARD_EXPIRED = "4000000000000069";

    public PaymentProcessorResponse charge(PaymentProcessorRequest paymentProcessorRequest) {

        String pan = paymentProcessorRequest.pan();

        if (PAN_CARD_DECLINED.equals(pan)) {
            log.warn("Card declined");
            return new PaymentProcessorResponse.Failure("CARD_DECLINED", "Card declined by bank");
        }

        if (PAN_CARD_EXPIRED.equals(pan)) {
            log.warn("Pan card has expired");
            return new PaymentProcessorResponse.Failure("CARD_EXPIRED", "Card has expired");
        }

        String processorRef = "CARD_PROCESSOR_"+ RandomizerUtil.randomBase64(16);

        return new PaymentProcessorResponse.Pending(processorRef);


    }
}
