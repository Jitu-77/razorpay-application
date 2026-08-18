package com.jitu.razorpay_application.payment_service.service.implementation;

//import com.jitu.RazorPay.common.enums.PaymentStatus;
//import com.jitu.RazorPay.payment.api.PaymentLookupService;
//import com.jitu.RazorPay.payment.entity.Payment;
//import com.jitu.RazorPay.payment.repository.PaymentRepository;


import com.jitu.razorpay_application.common_lib.enums.PaymentStatus;
import com.jitu.razorpay_application.payment_service.api.PaymentLookupService;
import com.jitu.razorpay_application.payment_service.entity.Payment;
import com.jitu.razorpay_application.payment_service.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentLookupServiceImpl  implements PaymentLookupService {
    private final PaymentRepository paymentRepository;

    @Override
    public List<Payment> findUnsettledCapturedPayments(UUID merchantId) {
        return paymentRepository.findByMerchantIdAndStatusForUpdate(merchantId, PaymentStatus.CAPTURED);
    }
}
