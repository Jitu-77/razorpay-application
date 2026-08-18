package com.jitu.razorpay_application.payment_service.config;

//import com.jitu.RazorPay.common.enums.PaymentMethod;
//import com.jitu.RazorPay.payment.gateway.PaymentAdapter;
//import com.jitu.RazorPay.payment.gateway.adatpter.CardPaymentAdapter;
//import com.jitu.RazorPay.payment.gateway.adatpter.NetBankingPaymentAdapter;
//import com.jitu.RazorPay.payment.gateway.adatpter.UpiPaymentAdapter;

import com.jitu.razorpay_application.common_lib.enums.PaymentMethod;
import com.jitu.razorpay_application.payment_service.gateway.PaymentAdapter;
import com.jitu.razorpay_application.payment_service.gateway.adatpter.CardPaymentAdapter;
import com.jitu.razorpay_application.payment_service.gateway.adatpter.NetBankingPaymentAdapter;
import com.jitu.razorpay_application.payment_service.gateway.adatpter.UpiPaymentAdapter;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class PaymentAdapterConfig {

    private final NetBankingPaymentAdapter netBankingAdapter;
    private final CardPaymentAdapter cardPaymentAdapter;
    private final UpiPaymentAdapter upiPaymentAdapter;
    @Bean
    public Map<PaymentMethod, PaymentAdapter> paymentAdapterMap(){
//            return Map.of(
//                    PaymentMethod.CARD , new CardPaymentAdapter(),
//                    PaymentMethod.NETBANKING,new NetBankingPaymentAdapter(),
//                    PaymentMethod.UPI , new UpiPaymentAdapter()
//            );


        // above is commented as DI will fail SPB wont take of this
        return Map.of(
                PaymentMethod.CARD , cardPaymentAdapter,
                PaymentMethod.NETBANKING,netBankingAdapter,
                PaymentMethod.UPI , upiPaymentAdapter
        );
    }
}
