package com.jitu.razorpay_application.operations_service.repository;


import com.jitu.razorpay_application.common_lib.enums.WebhookEventStatus;
import com.jitu.razorpay_application.operations_service.entity.WebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface WebhookEventRepository extends JpaRepository<WebhookEvent, UUID> {
    List<WebhookEvent> findByStatusAndNextRetryAtBefore(WebhookEventStatus webhookEventStatus, LocalDateTime now);
}
