package com.jitu.razorpay_application.merchant_service.mapper;

import com.jitu.razorpay_application.merchant_service.dto.request.MerchantSignupRequest;
import com.jitu.razorpay_application.merchant_service.dto.response.MerchantResponse;
import com.jitu.razorpay_application.merchant_service.entity.Merchant;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MerchantMapper {

    Merchant toEntityResponse(MerchantSignupRequest merchantSignupRequest);

    MerchantResponse toResponse(Merchant merchant);

}
