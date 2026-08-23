package com.jitu.razorpay_application.payment_service.service.implementation;

//import com.jitu.RazorPay.common.enums.EventAggregateType;
//import com.jitu.RazorPay.common.enums.OrderStatus;
//import com.jitu.RazorPay.common.exceptions.BusinessRuleViolationException;
//import com.jitu.RazorPay.common.exceptions.DuplicateResourceException;
//import com.jitu.RazorPay.common.exceptions.ResourceNotFoundException;
//import com.jitu.RazorPay.merchant.service.CustomerService;
//import com.jitu.RazorPay.payment.dto.request.CreateOrderRequest;
//import com.jitu.RazorPay.payment.dto.response.OrderResponse;
//import com.jitu.RazorPay.payment.dto.response.PaymentResponse;
//import com.jitu.RazorPay.payment.entity.OrderRecord;
//import com.jitu.RazorPay.payment.entity.Payment;
//import com.jitu.RazorPay.payment.mapper.OrderMapper;
//import com.jitu.RazorPay.payment.mapper.PaymentMapper;
//import com.jitu.RazorPay.payment.outbox.OutboxEventPublisher;
//import com.jitu.RazorPay.payment.repository.OrderRepository;
//import com.jitu.RazorPay.payment.repository.PaymentRepository;
//import com.jitu.RazorPay.payment.service.OrderService;




import com.jitu.razorpay_application.common_lib.dto.FindOrCreateCustomerRequest;
import com.jitu.razorpay_application.common_lib.enums.EventAggregateType;
import com.jitu.razorpay_application.common_lib.enums.OrderStatus;
import com.jitu.razorpay_application.common_lib.exceptions.BusinessRuleViolationException;
import com.jitu.razorpay_application.common_lib.exceptions.DuplicateResourceException;
import com.jitu.razorpay_application.common_lib.exceptions.ResourceNotFoundException;
import com.jitu.razorpay_application.payment_service.client.CustomerServiceClient;
import com.jitu.razorpay_application.payment_service.dto.request.CreateOrderRequest;
import com.jitu.razorpay_application.payment_service.dto.response.OrderResponse;
import com.jitu.razorpay_application.payment_service.dto.response.PaymentResponse;
import com.jitu.razorpay_application.payment_service.entity.OrderRecord;
import com.jitu.razorpay_application.payment_service.entity.Payment;
import com.jitu.razorpay_application.payment_service.mapper.OrderMapper;
import com.jitu.razorpay_application.payment_service.mapper.PaymentMapper;
import com.jitu.razorpay_application.payment_service.outbox.OutboxEventPublisher;
import com.jitu.razorpay_application.payment_service.repository.OrderRepository;
import com.jitu.razorpay_application.payment_service.repository.PaymentRepository;
import com.jitu.razorpay_application.payment_service.service.OrderService;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final OrderMapper orderMapper;
//    private final CustomerService customerService;
    private  final CustomerServiceClient customerServiceClient;
    private final OutboxEventPublisher eventPublisher;

    @Value("${payment.order.default-order-expiry-minutes:30}")
    private int orderExpiryInMinutes;
    
    @Override
    @Transactional
    public OrderResponse create(UUID merchantId, CreateOrderRequest request) {

            if(request.receipt()!= null &&
                    orderRepository.existsByMerchantIdAndReceipt(merchantId,request.receipt())){
                throw new DuplicateResourceException("ORDER_RECEIPT_DUPLICATE","Order with reciept already exists"+request.receipt());
            }

        UUID customerId = null;
        if (request.customer() != null) {
//            customerId = customerService.findOrCreate(merchantId,
//                    request.customer().email(),
//                    request.customer().name(),
//                    request.customer().phone()
//            );
            customerId = customerServiceClient.findOrCreate(
                    new FindOrCreateCustomerRequest(
                            merchantId,
                            request.customer().email(),
                            request.customer().name(),
                            request.customer().phone()
                    )
            );
        }

        // we will not be using mapStruct here as there is a lot of customization already done
        OrderRecord orderRecord = OrderRecord.builder()
                .receipt(request.receipt())
                .amount(request.amount())
                .notes(request.notes())
                .merchantId(merchantId)
                .customerId(customerId) //added customer
                .orderStatus(OrderStatus.CREATED)
                .expiresAt(request.expiresAt()!= null ? request.expiresAt() :
                        LocalDateTime.now().plusMonths(orderExpiryInMinutes))
                .build();
        orderRecord = orderRepository.save(orderRecord);
        // TODO:        publish kafka event about order creation
        // save to outbox for kafka  ----------
        eventPublisher.publish(EventAggregateType.ORDER, orderRecord.getId(),
                "ORDER_CREATED",
                Map.of("orderId", orderRecord.getId(),
                        "merchantId", merchantId.toString(),
                        "orderStatus", orderRecord.getOrderStatus().name(),
                        "amountUnits", orderRecord.getAmount().getAmountUnits(),
                        "amountCurrency", orderRecord.getAmount().getCurrency()
                )
        );
        // save to outbox for kafka ----------


        //replaced by orderMapper
//        return new OrderResponse(orderRecord.getId(),
//                orderRecord.getMerchantId(),
//                orderRecord.getReceipt(),
//                orderRecord.getAmount(),
//                orderRecord.getOrderStatus(),
//                orderRecord.getAttempts(),
//                orderRecord.getNotes(),
//                orderRecord.getExpiresAt(),
//                null
//        );
        return orderMapper.toResponse(orderRecord);
    }

    @Override
    public OrderResponse getById(UUID merchantId, UUID orderId) {
        OrderRecord orderRecord =  orderRepository.findByIdAndMerchantId(orderId,merchantId)
                                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
//        return new OrderResponse(orderRecord.getId(),orderRecord.getMerchantId(),
//                orderRecord.getReceipt(),orderRecord.getAmount(),
//                orderRecord.getOrderStatus(),orderRecord.getAttempts(),
//                orderRecord.getNotes(),orderRecord.getExpiresAt(),null);
        return  orderMapper.toResponse(orderRecord);
    };

    @Override
    @Transactional
    public OrderResponse cancel(UUID merchantId, UUID orderId) {
        OrderRecord orderRecord =  orderRepository.findByIdAndMerchantId(orderId,merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
        if(orderRecord.getOrderStatus() == OrderStatus.CANCELLED ||
            orderRecord.getOrderStatus() == OrderStatus.PAID){
                throw new BusinessRuleViolationException("ORDER_CANNOT_CANCEL",
                        "Cannot cancel Order with Status"+orderRecord.getOrderStatus().name());
        }
        orderRecord.setOrderStatus(OrderStatus.CANCELLED);
        orderRecord = orderRepository.save(orderRecord);

        //save to outbox for kafka---------
        eventPublisher.publish(EventAggregateType.ORDER, orderRecord.getId(),
                "ORDER_CANCELLED",
                Map.of("orderId", orderRecord.getId(),
                        "merchantId", merchantId.toString(),
                        "orderStatus", orderRecord.getOrderStatus().name(),
                        "amountUnits", orderRecord.getAmount().getAmountUnits(),
                        "amountCurrency", orderRecord.getAmount().getCurrency()
                )
        );
        //save to outbox for kafka ---------


//        return new OrderResponse(orderRecord.getId(),orderRecord.getMerchantId(),
//                orderRecord.getReceipt(),orderRecord.getAmount(),
//                orderRecord.getOrderStatus(),orderRecord.getAttempts(),
//                orderRecord.getNotes(),orderRecord.getExpiresAt(),null);
        return orderMapper.toResponse(orderRecord);
    }

    @Override
    public List<PaymentResponse> listPayment(UUID merchantId, UUID orderId) {
        OrderRecord orderRecord =  orderRepository.findByIdAndMerchantId(orderId,merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        List<Payment> paymentList  = paymentRepository.findByOrder_Id(orderRecord);
        //payment -- order -- orderId

//        return paymentList.stream()
//                .map(payment -> paymentMapper.toResponse(payment))
//                .collect(Collectors.toList());
        //mapstruct
        return paymentMapper.toResponseList(paymentList);
//            return null;
    }
}
