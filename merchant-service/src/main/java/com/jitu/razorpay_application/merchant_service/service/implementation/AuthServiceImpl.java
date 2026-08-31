package com.jitu.razorpay_application.merchant_service.service.implementation;

import com.jitu.razorpay_application.common_lib.enums.MerchantStatus;
import com.jitu.razorpay_application.common_lib.enums.UserRole;
import com.jitu.razorpay_application.common_lib.exceptions.BusinessRuleViolationException;
import com.jitu.razorpay_application.common_lib.exceptions.DuplicateResourceException;
import com.jitu.razorpay_application.common_lib.exceptions.ResourceNotFoundException;
import com.jitu.razorpay_application.merchant_service.dto.request.LoginRequest;
import com.jitu.razorpay_application.merchant_service.dto.request.MerchantSignupRequest;
import com.jitu.razorpay_application.merchant_service.dto.response.LoginResponse;
import com.jitu.razorpay_application.merchant_service.dto.response.MerchantResponse;
import com.jitu.razorpay_application.merchant_service.entity.AppUser;
import com.jitu.razorpay_application.merchant_service.entity.Merchant;
import com.jitu.razorpay_application.merchant_service.mapper.MerchantMapper;
import com.jitu.razorpay_application.merchant_service.repository.AppUserRepository;
import com.jitu.razorpay_application.merchant_service.repository.MerchantRepository;
import com.jitu.razorpay_application.merchant_service.security.JwtUtil;
import com.jitu.razorpay_application.merchant_service.service.AuthService;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final MerchantRepository merchantRepository;
    private final AppUserRepository appUserRepository;
    private final MerchantMapper merchantMapper;

    private final PasswordEncoder passwordEncoder;
//    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    @Override
    @Transactional
    public MerchantResponse signUp(MerchantSignupRequest request) {
        if(merchantRepository.existsByEmail(request.email())){
//            throw  new RuntimeException("Merchant already exists");
            throw  new DuplicateResourceException("DUPLICATE_MERCHANT_MAIL","Merchant already exists "+request.email());
        }
        //before using mapper
//        Merchant merchant = Merchant.builder()
//                .businessName(request.businessName())
//                .businessType(request.businessType())
//                .name(request.name())
//                .email(request.email())
//                .status(MerchantStatus.PENDING_KYC)
//                .build();

        //after using mapper
            Merchant merchant =merchantMapper.toEntityResponse(request);
                merchant.setStatus(MerchantStatus.PENDING_KYC);


                merchantRepository.save(merchant);

        AppUser appUser = AppUser.builder()
                .email(request.email())
                .merchant(merchant)
//                .passwordHash(request.password()) // TODO: encrypt using Bcrypt
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(UserRole.OWNER)
                .build();

        appUserRepository.save(appUser);
//before using mapper
//        return new MerchantResponse(
//                merchant.getId(), merchant.getName(),
//                merchant.getEmail(), merchant.getBusinessName(),
//                merchant.getBusinessType(), merchant.getStatus()
//        );
        //after using mapper
        return  merchantMapper.toResponse(merchant);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        //if something unexpected occurs this throws exception directly
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(request.email(), request.password())
//        );

        AppUser appUser = appUserRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.email()));

//
        if (!passwordEncoder.matches(request.password(), appUser.getPasswordHash())) {
            throw new BusinessRuleViolationException("INVALID_CREDENTIALS", "Invalid email or password");
        }

        String token = jwtUtil.generateAccessToken(request.email(), appUser.getMerchant().getId(), appUser.getRole().toString());

        return new LoginResponse(token);
    }
}
