package com.jitu.razorpay_application.payment_service.service.implementation;

//import com.jitu.RazorPay.common.enums.PaymentStatus;
//import com.jitu.RazorPay.payment.api.PaymentLookupService;
//import com.jitu.RazorPay.payment.entity.Payment;
//import com.jitu.RazorPay.payment.repository.PaymentRepository;


import com.jitu.razorpay_application.common_lib.dto.PaymentSettlementView;
import com.jitu.razorpay_application.common_lib.enums.PaymentStatus;
import com.jitu.razorpay_application.payment_service.api.PaymentLookupService;
import com.jitu.razorpay_application.payment_service.entity.Payment;
import com.jitu.razorpay_application.payment_service.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentLookupServiceImpl  implements PaymentLookupService {
    private final PaymentRepository paymentRepository;

//    @Override
//    public List<Payment> findUnsettledCapturedPayments(UUID merchantId) {
//        return paymentRepository.findByMerchantIdAndStatusForUpdate(merchantId, PaymentStatus.CAPTURED);
//    }

    @Override
    @Transactional
    public List<PaymentSettlementView> findUnsettledCapturedPayments(UUID merchantId) {
        List<Payment> paymentList = paymentRepository
                .findByMerchantIdAndStatusForUpdate(merchantId, PaymentStatus.CAPTURED);

        return paymentList.stream()
                .map(p -> new PaymentSettlementView(
                        p.getId(),
                        p.getAmount().getAmountUnits(),
                        0, // TODO: replace with actual refund values from RefundRepository
                        p.getAmount().getCurrency()))
                .toList();
    }

    @Override
    @Transactional
    public void markSettled(List<UUID> paymentList) {
        LocalDateTime now = LocalDateTime.now();
        List<Payment> payments = paymentRepository.findAllById(paymentList);
        for (Payment payment : payments) {
            payment.setStatus(PaymentStatus.SETTLED);
            payment.setSettledAt(now);
        }
        paymentRepository.saveAll(payments);
    }
}
