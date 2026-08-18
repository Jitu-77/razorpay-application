package com.jitu.razorpay_application.merchant_service.service;

import com.jitu.razorpay_application.merchant_service.dto.request.LoginRequest;
import com.jitu.razorpay_application.merchant_service.dto.request.MerchantSignupRequest;
import com.jitu.razorpay_application.merchant_service.dto.response.LoginResponse;
import com.jitu.razorpay_application.merchant_service.dto.response.MerchantResponse;

public interface AuthService {
     MerchantResponse signUp(MerchantSignupRequest request);
     LoginResponse login(LoginRequest request);
}
