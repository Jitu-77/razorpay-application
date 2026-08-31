//package com.jitu.razorpay_application.merchant_service.security;
//
//import com.jitu.razorpay_application.common_lib.exceptions.ResourceNotFoundException;
//import com.jitu.razorpay_application.merchant_service.repository.AppUserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//
//@Service
//@RequiredArgsConstructor
//public class MerchantUserDetailsService implements UserDetailsService {
//
//    private final AppUserRepository appUserRepository;
//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        return appUserRepository.findByEmail(email)
//                .orElseThrow(() -> new ResourceNotFoundException("User", email));
//    }
//}
