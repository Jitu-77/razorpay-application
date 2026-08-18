package com.jitu.razorpay_application.operations_service.repository;

import com.jitu.razorpay_application.operations_service.entity.SettlementPayment;
import com.jitu.razorpay_application.operations_service.entity.SettlementPaymentId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementPaymentRepository extends JpaRepository<SettlementPayment, SettlementPaymentId> {
}