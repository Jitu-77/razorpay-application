package com.jitu.razorpay_application.operations_service.settlement;

//import com.jitu.RazorPay.common.dto.SettlementBankDetails;
//import com.jitu.RazorPay.common.entity.Money;
//import com.jitu.RazorPay.common.enums.EventAggregateType;
//import com.jitu.RazorPay.common.enums.SettlementStatus;
//import com.jitu.RazorPay.common.exceptions.ResourceNotFoundException;
//import com.jitu.RazorPay.merchant.api.MerchantLookupService;
//import com.jitu.RazorPay.operations.entity.Settlement;
//import com.jitu.RazorPay.operations.entity.SettlementPayment;
//import com.jitu.RazorPay.operations.entity.SettlementPaymentId;
//import com.jitu.RazorPay.operations.repository.SettlementPaymentRepository;
//import com.jitu.RazorPay.operations.repository.SettlementRepository;
//import com.jitu.RazorPay.operations.settlement.dto.BankTransferResult;
//import com.jitu.RazorPay.payment.api.PaymentLookupService;
//import com.jitu.RazorPay.payment.entity.Payment;
//import com.jitu.RazorPay.payment.outbox.OutboxEventPublisher;


import com.jitu.razorpay_application.common_lib.dto.PaymentSettlementView;
import com.jitu.razorpay_application.common_lib.dto.SettlementBankDetails;
import com.jitu.razorpay_application.common_lib.entity.Money;
import com.jitu.razorpay_application.common_lib.enums.EventAggregateType;
import com.jitu.razorpay_application.common_lib.enums.SettlementStatus;
import com.jitu.razorpay_application.common_lib.exceptions.ResourceNotFoundException;
import com.jitu.razorpay_application.operations_service.client.MerchantServiceClient;
import com.jitu.razorpay_application.operations_service.client.PaymentServiceClient;
import com.jitu.razorpay_application.operations_service.entity.Settlement;
import com.jitu.razorpay_application.operations_service.entity.SettlementPayment;
import com.jitu.razorpay_application.operations_service.entity.SettlementPaymentId;
import com.jitu.razorpay_application.operations_service.outbox.OutboxEventPublisher;
import com.jitu.razorpay_application.operations_service.repository.SettlementPaymentRepository;
import com.jitu.razorpay_application.operations_service.repository.SettlementRepository;
import com.jitu.razorpay_application.operations_service.settlement.dto.BankTransferResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SettlementTransactionExecutor {
    private static final double FEE_RATE = 0.02;
    private static final double GST_RATE = 0.18;
//    private final PaymentLookupService paymentLookupService;
    private final SettlementRepository settlementRepository;
    private final SettlementPaymentRepository settlementPaymentRepository;
//    private final MerchantLookupService merchantLookupService;
    private final BankTransferProcessor bankTransferProcessor;
    // Todo: publisher inside it's own db
    private final OutboxEventPublisher outboxEventPublisher;
    private  final MerchantServiceClient merchantServiceClient;
    private  final PaymentServiceClient paymentServiceClient;
    @Transactional
    public void processForMerchant(UUID merchantId, LocalDate settlementDate) {
//        List<Payment> unsettledPayments = paymentLookupService.findUnsettledCapturedPayments(merchantId);
        List<PaymentSettlementView> unsettledPayments = paymentServiceClient.findUnsettledCaptured(merchantId);
        if (unsettledPayments.isEmpty()) return;

        log.info("Processing {} unsettled payments for merchantId: {} on {} date",
                unsettledPayments.size(), merchantId, settlementDate);

//        Money gross = unsettledPayments.stream()
//                .map(Payment::getAmount)
//                .reduce(Money::add)
//                .orElseThrow();

        Integer grossAmount = unsettledPayments.stream()
                .map(PaymentSettlementView::amountUnits)
                .reduce(Integer::sum)
                .orElse(0);

        Money gross = Money.of(grossAmount, unsettledPayments.getFirst().currency());


        int fee = Math.toIntExact(Math.round(gross.getAmountUnits() * FEE_RATE));
        int gst = Math.toIntExact(Math.round(fee * GST_RATE));
        Money feeAmount = Money.of(fee, gross.getCurrency());
        Money gstAmount = Money.of(gst, gross.getCurrency());
        Money netAmount = gross.subtract(feeAmount).subtract(gstAmount);

        Settlement settlement = Settlement.builder()
                .merchantId(merchantId)
                .grossAmount(gross)
                .feeAmount(feeAmount)
                .gstAmount(gstAmount)
                .netAmount(netAmount)
                .status(SettlementStatus.INITIATED)
                .build();

        settlementRepository.save(settlement);

        try {
            List<SettlementPayment> links = new ArrayList<>();
//            for (Payment p : unsettledPayments) {
//                links.add(SettlementPayment.builder()
//                        .id(new SettlementPaymentId(settlement.getId(),p.getId()))
//                        .settlement(settlement)
//                        .build());
//            }

            for (PaymentSettlementView p : unsettledPayments) {
                links.add(SettlementPayment.builder()
                        .id(new SettlementPaymentId(settlement.getId(),p.paymentId()))
                        .settlement(settlement)
                        .build());
            }



            settlementPaymentRepository.saveAll(links);


            // also updating the payment service
            List<UUID> paymentId =unsettledPayments.stream()
                    .map(PaymentSettlementView::paymentId).toList();
            paymentServiceClient.markSettled(paymentId);

//            SettlementBankDetails settlementBankDetails = merchantLookupService.getSettlementBankDetails(merchantId);
            SettlementBankDetails settlementBankDetails = merchantServiceClient.getSettlementBankDetails(merchantId);
            BankTransferResult bankTransferResult = bankTransferProcessor.initiate(settlement.getId(), merchantId, netAmount,
                    settlementBankDetails.accountNumber(), settlementBankDetails.ifsc());

            settlement.setStatus(SettlementStatus.TRANSFER_PENDING);
            settlement.setBankReference(bankTransferResult.registrationRef());

            settlementRepository.save(settlement);
        } catch (Exception e) {
            log.error("Settlement failed for settlementId: {} on date: {}", settlement.getId(), settlementDate, e);
            settlement.setStatus(SettlementStatus.FAILED);
            settlementRepository.save(settlement);
        }
    }


    @Transactional
    public void resolveTransfer(UUID settlementId,
                                String errorCode, String errorDescription) {

        Settlement settlement = settlementRepository.findById(settlementId).orElseThrow(
                () -> new ResourceNotFoundException("Settlement", settlementId));

        if (settlement.getStatus() != SettlementStatus.TRANSFER_PENDING) {
            log.info("Settlement resolved, skipping for id: {}", settlement.getId());
            return;
        }

        if (errorCode == null) { // success
            settlement.setStatus(SettlementStatus.PROCESSED);
            settlement.setProcessedAt(LocalDateTime.now());
            settlementRepository.save(settlement);
            log.info("Settlement processed successfully, settlementId: {}", settlement.getId());
            outboxEventPublisher.publish(EventAggregateType.SETTLEMENT, settlementId,
                    "SETTLEMENT_PROCESSED", Map.of(
                            "settlementId", settlement,
                            "merchantId", settlement.getMerchantId(),
                            "status", settlement.getStatus().name(),
                            "settlementAmount", settlement.getNetAmount().getAmountUnits(),
                            "settlementCurrency", settlement.getNetAmount().getCurrency()
                    ));
        } else { // failed
            settlement.setStatus(SettlementStatus.FAILED);
            settlement.setFailureReason(errorCode+" : "+errorDescription);
            settlementRepository.save(settlement);
            log.warn("Settlement failed, settlementId: {}", settlement.getId());
            outboxEventPublisher.publish(EventAggregateType.SETTLEMENT, settlementId,
                    "SETTLEMENT_FAILED", Map.of(
                            "settlementId", settlement,
                            "merchantId", settlement.getMerchantId(),
                            "status", settlement.getStatus().name(),
                            "settlementAmount", settlement.getNetAmount().getAmountUnits(),
                            "settlementCurrency", settlement.getNetAmount().getCurrency()
                    ));
        }

    }
}
