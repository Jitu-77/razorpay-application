package com.jitu.razorpay_application.payment_service.repository;


//import com.jitu.RazorPay.payment.entity.OutboxEvent;
import com.jitu.razorpay_application.common_lib.enums.OutboxStatus;
import com.jitu.razorpay_application.payment_service.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    List<OutboxEvent> findByStatusOrderByCreatedAtAsc(OutboxStatus status);
}
