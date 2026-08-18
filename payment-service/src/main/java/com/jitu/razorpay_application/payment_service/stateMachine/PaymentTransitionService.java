package com.jitu.razorpay_application.payment_service.stateMachine;

//import com.jitu.RazorPay.common.enums.PaymentActor;
//import com.jitu.RazorPay.common.enums.PaymentEvent;
//import com.jitu.RazorPay.common.enums.PaymentStatus;
//import com.jitu.RazorPay.payment.entity.Payment;
//import com.jitu.RazorPay.payment.entity.PaymentTransitionLog;
//import com.jitu.RazorPay.payment.repository.PaymentTransitionLogRepository;

import com.jitu.razorpay_application.common_lib.enums.PaymentActor;
import com.jitu.razorpay_application.common_lib.enums.PaymentEvent;
import com.jitu.razorpay_application.common_lib.enums.PaymentStatus;
import com.jitu.razorpay_application.payment_service.entity.Payment;
import com.jitu.razorpay_application.payment_service.entity.PaymentTransitionLog;
import com.jitu.razorpay_application.payment_service.repository.PaymentTransitionLogRepository;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentTransitionService {
    private final PaymentTransitionLogRepository paymentTransitionLogRepository;
    private final PaymentStateMachine paymentStateMachine;
    public PaymentStatus apply(Payment payment, PaymentEvent event) {
        PaymentStatus next = paymentStateMachine.transition(payment.getStatus(), event);
        PaymentTransitionLog log = PaymentTransitionLog.builder()
                .payment(payment)
                .fromStatus(payment.getStatus())
                .event(event)
                .toStatus(next)
                .actor(PaymentActor.SYSTEM) //TODO: fetch merchant context to identify actor
                .occurredAt(LocalDateTime.now())
                .build();
        payment.setStatus(next); // source of truth is in only one place
        paymentTransitionLogRepository.save(log);
        return next;
    }
}
