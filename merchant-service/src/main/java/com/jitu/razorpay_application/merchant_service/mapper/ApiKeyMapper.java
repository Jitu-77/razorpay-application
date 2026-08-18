package com.jitu.razorpay_application.merchant_service.mapper;



import com.jitu.razorpay_application.merchant_service.dto.response.ApiKeyCreateResponse;
import com.jitu.razorpay_application.merchant_service.dto.response.ApiKeyResponse;
import com.jitu.razorpay_application.merchant_service.entity.ApiKey;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ApiKeyMapper {
    ApiKeyCreateResponse toCreateResonse(ApiKey apiKey);

    List<ApiKeyResponse> toResponseList(List<ApiKey> apiKeyList);
}
