package com.jitu.razorpay_application.payment_service.config;

//import com.jitu.RazorPay.common.enums.PaymentMethod;
//import com.jitu.RazorPay.payment.processor.PaymentProcessor;
//import com.jitu.RazorPay.payment.processor.strategy.CardPaymentProcessor;
//import com.jitu.RazorPay.payment.processor.strategy.NetBankingPaymentProcessor;
//import com.jitu.RazorPay.payment.processor.strategy.UpiPaymentProcessor;

import com.jitu.razorpay_application.common_lib.enums.PaymentMethod;
import com.jitu.razorpay_application.payment_service.processor.PaymentProcessor;
import com.jitu.razorpay_application.payment_service.processor.strategy.CardPaymentProcessor;
import com.jitu.razorpay_application.payment_service.processor.strategy.NetBankingPaymentProcessor;
import com.jitu.razorpay_application.payment_service.processor.strategy.UpiPaymentProcessor;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class PaymentProcessorConfig {
    private final CardPaymentProcessor cardPaymentProcessor;
    private final NetBankingPaymentProcessor netBankingPaymentProcessor;
    private final UpiPaymentProcessor upiPaymentProcessor;
    @Bean
    public Map<PaymentMethod, PaymentProcessor> paymentProcessorMap(){
        return  Map.of(
//            PaymentMethod.CARD, new CardPaymentProcessor(),
//            PaymentMethod.NETBANKING, new NetBankingPaymentProcessor(),
//            PaymentMethod.UPI, new UpiPaymentProcessor()


                PaymentMethod.CARD, cardPaymentProcessor,
                PaymentMethod.NETBANKING, netBankingPaymentProcessor,
                PaymentMethod.UPI, upiPaymentProcessor
        );
    }
}
