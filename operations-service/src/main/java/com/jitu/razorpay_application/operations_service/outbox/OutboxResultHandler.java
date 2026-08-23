package com.jitu.razorpay_application.operations_service.outbox;


//import com.jitu.RazorPay.common.enums.OutboxStatus;
//import com.jitu.RazorPay.payment.entity.OutboxEvent;
//import com.jitu.RazorPay.payment.repository.OutboxEventRepository;


import com.jitu.razorpay_application.common_lib.enums.OutboxStatus;
//import com.jitu.razorpay_application.payment_service.entity.OutboxEvent;
//import com.jitu.razorpay_application.payment_service.repository.OutboxEventRepository;
import com.jitu.razorpay_application.operations_service.entity.OutboxEvent;
import com.jitu.razorpay_application.operations_service.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class OutboxResultHandler {

    private final OutboxEventRepository outboxEventRepository;
    private final Integer MAX_ATTEMPTS = 3;

    @Transactional
    public void handleEventPublished(OutboxEvent event) {
        event.setStatus(OutboxStatus.PUBLISHED);
        event.setPublishedAt(LocalDateTime.now());
        outboxEventRepository.save(event);
    }

    @Transactional
    public void handleEventFailed(OutboxEvent event, String errorMessage) {
        event.setAttempts(event.getAttempts()+1);
        event.setLastError(
                errorMessage.length() < 1000 ? errorMessage: errorMessage.substring(0, 1000));
        if (event.getAttempts() >= MAX_ATTEMPTS) {
            event.setStatus(OutboxStatus.FAILED);
        }
        outboxEventRepository.save(event);
    }
}








