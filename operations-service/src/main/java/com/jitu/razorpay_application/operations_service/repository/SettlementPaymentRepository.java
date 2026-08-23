package com.jitu.razorpay_application.operations_service.repository;

import com.jitu.razorpay_application.operations_service.entity.Settlement;
import com.jitu.razorpay_application.operations_service.entity.SettlementPayment;
import com.jitu.razorpay_application.operations_service.entity.SettlementPaymentId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SettlementPaymentRepository extends JpaRepository<SettlementPayment, SettlementPaymentId> {
    List<SettlementPayment> findBySettlement(Settlement settlement);
}