package com.jitu.razorpay_application.payment_service.service.implementation;

//import com.jitu.RazorPay.common.enums.*;
//import com.jitu.RazorPay.common.exceptions.BusinessRuleViolationException;
//import com.jitu.RazorPay.common.exceptions.ResourceNotFoundException;
//import com.jitu.RazorPay.payment.dto.request.PaymentInitRequest;
//import com.jitu.RazorPay.payment.dto.response.PaymentResponse;
//import com.jitu.RazorPay.payment.entity.OrderRecord;
//import com.jitu.RazorPay.payment.entity.Payment;
//import com.jitu.RazorPay.payment.gateway.PaymentGatewayRouter;
//import com.jitu.RazorPay.payment.gateway.dto.PaymentRequest;
//import com.jitu.RazorPay.payment.gateway.dto.PaymentResult;
//import com.jitu.RazorPay.payment.mapper.PaymentMapper;
//import com.jitu.RazorPay.payment.outbox.OutboxEventPublisher;
//import com.jitu.RazorPay.payment.repository.OrderRepository;
//import com.jitu.RazorPay.payment.repository.PaymentRepository;
//import com.jitu.RazorPay.payment.service.PaymentService;
//import com.jitu.RazorPay.payment.stateMachine.PaymentTransitionService;


import com.jitu.razorpay_application.common_lib.enums.EventAggregateType;
import com.jitu.razorpay_application.common_lib.enums.OrderStatus;
import com.jitu.razorpay_application.common_lib.enums.PaymentEvent;
import com.jitu.razorpay_application.common_lib.enums.PaymentStatus;
import com.jitu.razorpay_application.common_lib.exceptions.BusinessRuleViolationException;
import com.jitu.razorpay_application.common_lib.exceptions.ResourceNotFoundException;
import com.jitu.razorpay_application.payment_service.dto.request.PaymentInitRequest;
import com.jitu.razorpay_application.payment_service.dto.response.PaymentResponse;
import com.jitu.razorpay_application.payment_service.entity.OrderRecord;
import com.jitu.razorpay_application.payment_service.entity.Payment;
import com.jitu.razorpay_application.payment_service.gateway.PaymentGatewayRouter;
import com.jitu.razorpay_application.payment_service.gateway.dto.PaymentRequest;
import com.jitu.razorpay_application.payment_service.gateway.dto.PaymentResult;
import com.jitu.razorpay_application.payment_service.mapper.PaymentMapper;
import com.jitu.razorpay_application.payment_service.outbox.OutboxEventPublisher;
import com.jitu.razorpay_application.payment_service.repository.OrderRepository;
import com.jitu.razorpay_application.payment_service.repository.PaymentRepository;
import com.jitu.razorpay_application.payment_service.saga.PaymentAuthorizationRecorder;
import com.jitu.razorpay_application.payment_service.service.PaymentService;
import com.jitu.razorpay_application.payment_service.stateMachine.PaymentTransitionService;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    public final OrderRepository orderRepository;
    public final PaymentRepository paymentRepository;
    public final PaymentGatewayRouter paymentGatewayRouter;
    public final PaymentMapper paymentMapper;
    public final PaymentTransitionService paymentTransitionService;
    private final OutboxEventPublisher eventPublisher;
    private final PaymentAuthorizationRecorder paymentAuthorizationRecorder;

    //applying saga pattern------------------
//    @Override
//    @Transactional
//    // (isolation = Isolation.REPEATABLE_READ) // not thread safe Pessimistic Locking will take care
//    // via REPEATABLE_READ we can still read but not write
//    public PaymentResponse intitiate(UUID merchantId, PaymentInitRequest paymentInitRequest) {
//        log.info(" ORDERID "+paymentInitRequest.orderId()+" MerchangtID "+merchantId);
//        //not thread safe-------------
////        OrderRecord orderRecord = orderRepository.findByIdAndMerchantId(paymentInitRequest.orderId(),
////                merchantId).orElseThrow(
////                ()-> new ResourceNotFoundException("Order", paymentInitRequest.orderId())
////        );
//
//        // for pessimistic locking ---------------
//        OrderRecord orderRecord = orderRepository.findByIdAndMerchantIdForUpdate(paymentInitRequest.orderId(), merchantId)
//                .orElseThrow(() -> new ResourceNotFoundException("Order", paymentInitRequest.orderId()));
//
//        if(orderRecord.getOrderStatus()!= OrderStatus.CREATED && orderRecord.getOrderStatus() != OrderStatus.ATTEMPTED){
//            throw new BusinessRuleViolationException("ORDER_NOT_PAYABLE",
//                    "Order cannot accept payment in status: "+orderRecord.getOrderStatus());
//        }
//
//        orderRecord.setOrderStatus(OrderStatus.ATTEMPTED);
//        orderRecord.setAttempts(orderRecord.getAttempts()+1);
//
//        Payment payment = Payment.builder()
//                .order(orderRecord)
//                .merchantId(merchantId)
//                .amount(orderRecord.getAmount())
//                .status(PaymentStatus.CREATED)// initiating the flow so its ok
//                .idempotencyKey(UUID.randomUUID().toString()) //TODO: idempotency
//                .method(paymentInitRequest.method())
//                .methodDetails(paymentInitRequest.methodDetails())
//                .build();
//
//        payment =   paymentRepository.save(payment);
//        // now for Payment gateway settings --------------------------------------
//        PaymentRequest paymentRequest = new PaymentRequest(payment.getId(),
//                paymentInitRequest.orderId(), merchantId,
//                orderRecord.getAmount(), paymentInitRequest.method(),
//                paymentInitRequest.methodDetails());
//        // need to go to transit to this state FIRST before we can transit to some other state -- AUTHORIZING
//        paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_ATTEMPT);
//
//        PaymentResult paymentResult =  paymentGatewayRouter.initiate(paymentRequest);
//        //------------------------------------------------------------------------
//
//        switch (paymentResult){
//            case PaymentResult.Pending pending -> payment.setProcessorReference(pending.registrationRef());
//            case PaymentResult.Failure failure ->{
////                payment.setStatus(PaymentStatus.FAILED);
//                paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_FAIL);
//                payment.setErrorCode(failure.errorCode());
//                payment.setErrorDescription(failure.errorDescription());
//            }
//            case PaymentResult.Success success -> {
//                log.warn("Invalid state");
//                return null;
//            }
//        }
//
//        payment = paymentRepository.save(payment);
//        orderRepository.save(orderRecord);
//        // TODO: send an outbox (kafka event)
//        // save to outbox for kafka  ----------
//        eventPublisher.publish(EventAggregateType.PAYMENT, payment.getId(),
//                "PAYMENT_CREATED",
//                Map.of("orderId", orderRecord.getId().toString(),
//                        "paymentId", payment.getId().toString(),
//                        "merchantId", merchantId.toString(),
//                        "paymentStatus", payment.getStatus().name(),
//                        "amountUnits", orderRecord.getAmount().getAmountUnits(),
//                        "amountCurrency", orderRecord.getAmount().getCurrency(),
//                        "paymentMethod", payment.getMethod()
//                )
//        );
//        // save to outbox for kafka ----------
//
//
//
//        return paymentMapper.toResponse(payment);
//    }
//applying saga pattern for the above method ------------------

@Override
public PaymentResponse intitiate(UUID merchantId, PaymentInitRequest paymentInitRequest) {
    Payment payment = paymentAuthorizationRecorder.recordPayment(merchantId, paymentInitRequest);
    PaymentRequest paymentRequest = new PaymentRequest(payment.getId(),
               paymentInitRequest.orderId(), merchantId,
                    payment.getAmount(), paymentInitRequest.method(),
                paymentInitRequest.methodDetails());
    PaymentResult result;
    try {
        result = paymentGatewayRouter.initiate(paymentRequest);
    } catch (Exception e) {
        return paymentAuthorizationRecorder.compensateAuthorizationFailure(payment.getId(),
                "PAYMENT_GATEWAY_ROUTER_UNREACHABLE", e.getMessage());
    }
    return paymentAuthorizationRecorder.applyGatewayResult(payment.getId(), result);
}



    @Override
    @Transactional // *
    public PaymentResponse capture(UUID merchantId, UUID paymentId){
        //not thread safe-------------
//        Payment payment = paymentRepository.findByIdAndMerchantId(paymentId, merchantId)
//                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));
        // for pessimistic locking ---------------
        Payment payment = paymentRepository.findByIdAndMerchantIdForUpdate(paymentId, merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));
        // for pessimistic locking ---------------


//        payment.setStatus(PaymentStatus.CAPTURING);
        paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_REQUEST); // introducing transition logs
        PaymentResult paymentResult = paymentGatewayRouter.capture(payment.getMethod(),paymentId);

        if(paymentResult instanceof  PaymentResult.Success success){
//            payment.setStatus(PaymentStatus.CAPTURED);
            paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_SUCCESS);
            payment.setCapturedAt(LocalDateTime.now());
            log.info("payment Captured ",paymentId);
        }else if (paymentResult instanceof  PaymentResult.Failure failure){
//            payment.setStatus(PaymentStatus.AUTHORIZED);
            paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_FAIL);
            payment.setErrorCode(failure.errorCode());
            payment.setErrorDescription(failure.errorDescription());
            log.warn("payment Captured failed ",paymentId);
        }

        payment = paymentRepository.save(payment);
        // TODO: send an outbox (kafka event)
        // save to outbox for kafka  ----------
        eventPublisher.publish(EventAggregateType.PAYMENT, payment.getId(),
                "PAYMENT_STATUS_CHANGED",
                Map.of("orderId", payment.getOrder().getId().toString(),
                        "paymentId", payment.getId().toString(),
                        "merchantId", merchantId.toString(),
                        "paymentStatus", payment.getStatus().name(),
                        "amountUnits", payment.getAmount().getAmountUnits(),
                        "amountCurrency", payment.getAmount().getCurrency(),
                        "paymentMethod", payment.getMethod()
                )
        );
        // save to outbox for kafka  ----------
        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional
    public void resolveAuthorization(UUID paymentId, boolean approve,
                                     String bankRef, String errorCode, String errorDescription) {

//        Payment payment = paymentRepository.findById(paymentId)
//                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));
// for pessimistic locking ---------------
        Payment payment = paymentRepository.findByIdForUpdate(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));
// for pessimistic locking ---------------
        if (payment.getStatus() != PaymentStatus.AUTHORIZING) {
            log.warn("Payment is not in Authorizing state, paymentID: {}, status: {}", paymentId, payment.getStatus());
            return;
        }

        OrderRecord orderRecord = payment.getOrder();

        if (approve) {
            paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_SUCCESS);
            payment.setBankReference(bankRef);
            payment.setAuthorizedAt(LocalDateTime.now());

            // Auto-capture--------
            paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_REQUEST);
            PaymentResult captureResult = paymentGatewayRouter.capture(payment.getMethod(), paymentId);

            if(captureResult instanceof PaymentResult.Success success) {
                paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_SUCCESS);
                payment.setCapturedAt(LocalDateTime.now());
                orderRecord.setOrderStatus(OrderStatus.PAID);
            } else if (captureResult instanceof  PaymentResult.Failure failure){
                paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_FAIL);
                payment.setErrorCode(failure.errorCode());
                payment.setErrorDescription(failure.errorDescription());
            }
            //--------------
        } else {
            paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_FAIL);
            payment.setErrorCode(errorCode);
            payment.setErrorDescription(errorDescription);
        }

        paymentRepository.save(payment);
        orderRepository.save(orderRecord);

        // TODO: send an outbox (kafka event)
        // save to outbox for kafka  ----------
        eventPublisher.publish(EventAggregateType.PAYMENT, payment.getId(), "PAYMENT_STATUS_CHANGED",
                Map.of("orderId", payment.getOrder().getId().toString(),
                        "paymentId", payment.getId().toString(),
                        "merchantId", payment.getMerchantId().toString(),
                        "paymentStatus", payment.getStatus().name(),
                        "amountUnits", payment.getAmount().getAmountUnits(),
                        "amountCurrency", payment.getAmount().getCurrency(),
                        "paymentMethod", payment.getMethod()
                )
        );
        // save to outbox for kafka  ----------

    }
}
// we have mapped our payment gateway here during initiate payment
// but dont mapped the same for processor