package com.jitu.razorpay_application.payment_service.mapper;

//import com.jitu.RazorPay.payment.dto.response.OrderResponse;
//import com.jitu.RazorPay.payment.entity.OrderRecord;


import com.jitu.razorpay_application.payment_service.dto.response.OrderResponse;
import com.jitu.razorpay_application.payment_service.entity.OrderRecord;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {
//    @Mapping(target = "",source = "")
    OrderResponse toResponse(OrderRecord orderRecord);
}
