package com.jitu.razorpay_application.payment_service.mapper;

//import com.jitu.RazorPay.payment.dto.response.PaymentResponse;
//import com.jitu.RazorPay.payment.entity.Payment;


import com.jitu.razorpay_application.payment_service.dto.response.PaymentResponse;
import com.jitu.razorpay_application.payment_service.entity.Payment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING) //componentModel = "Spring"
public interface PaymentMapper {
    @Mapping(target = "orderId", source = "order.id")
    PaymentResponse toResponse(Payment payment);

    @Mapping(target = "orderId", source = "order.id")
    List<PaymentResponse> toResponseList(List<Payment> paymentList);
}
