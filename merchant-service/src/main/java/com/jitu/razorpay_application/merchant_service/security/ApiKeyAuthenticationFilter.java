//package com.jitu.razorpay_application.merchant_service.security;
//
//import com.jitu.razorpay_application.common_lib.context.MerchantContext;
//import com.jitu.razorpay_application.common_lib.exceptions.RateLimitException;
//import com.jitu.razorpay_application.common_lib.ratelimit.RateLimitResult;
//import com.jitu.razorpay_application.common_lib.ratelimit.RateLimiter;
//import com.jitu.razorpay_application.merchant_service.cache.ApiKeyCache;
//import com.jitu.razorpay_application.merchant_service.cache.ApiKeyCacheEntry;
//import com.jitu.razorpay_application.merchant_service.entity.ApiKey;
//import com.jitu.razorpay_application.merchant_service.repository.ApiKeyRepository;
//
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.coyote.BadRequestException;
//import org.springframework.beans.factory.annotation.Value;
////import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
////import org.springframework.security.core.authority.SimpleGrantedAuthority;
////import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//import org.springframework.web.servlet.HandlerExceptionResolver;
//
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.util.Base64;
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {
//
//    private static final String BASIC_PREFIX = "Basic ";
//    private final ApiKeyRepository apiKeyRepository;
//    private final BCryptPasswordEncoder BCRYPT = new BCryptPasswordEncoder();
////    private final PasswordEncoder passwordEncoder; creates circular depedecy issue
//    private final MerchantContext merchantContext;
//    private final HandlerExceptionResolver handlerExceptionResolver;
//
//    //implementing cache
//    // as apikeycache has only one implementation hence it will be
//    //injected automatically
//    private final ApiKeyCache apiKeyCache;
//    private final RateLimiter rateLimiter;
//    @Value("${app.rate-limit.use-case.api-key.requests-per-minute:60}")
//    private Integer requestsPerMinute;
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//
//        log.info("Incoming request: {}", request.getRequestURI());
//
//        try {
//            String header = request.getHeader("Authorization");
//            if (header == null || !header.startsWith(BASIC_PREFIX)) {
//                filterChain.doFilter(request, response);
//                return;
//            }
//
////        Authorization: Basic key_asdlfjaosduf:secret_asdflauouadf
////        Authorization: Basic ASDFUAOSJDFLAKSJDFA89SDUFLIJalsdjflakjsdflk==
//
//            String[] credentials = decode(header);
//            if (credentials == null) {
//                throw new BadRequestException("Malformed API Key Header");
//            }
//
//            String keyId = credentials[0];
//            String rawSecret = credentials[1];
//// now we will be implementing caching ------------------------
////            ApiKey apiKey = apiKeyRepository.findByKeyId(keyId)
////                    .orElseThrow(() -> new BadRequestException("Invalid or missing API Key"));
////
////            if (!apiKey.isEnabled() || !secretMatches(rawSecret, apiKey)) {
////                throw new BadRequestException("Invalid or missing API Key");
////            }
////
////            var auth = new UsernamePasswordAuthenticationToken(keyId, null,
////                    List.of(new SimpleGrantedAuthority("API_KEY_ROLE"))
////            );
////
////            SecurityContextHolder.getContext().setAuthentication(auth);
////            merchantContext.setMerchantId(apiKey.getMerchant().getId());
////            merchantContext.setKeyId(apiKey.getKeyId());
//            // implementing basic caching starts-------------------------------------
//            ApiKeyCacheEntry apiKeyEntry = apiKeyCache.get(keyId)
//                    .orElseGet(() -> loadAndCache(keyId));
//            if (apiKeyEntry == null || !apiKeyEntry.enabled() || !secretMatches(rawSecret,apiKeyEntry)) {
//                throw new BadRequestException("Invalid or missing API Key");
//            }
//            RateLimitResult rateLimitResult = rateLimiter.check("apikey:"+keyId, requestsPerMinute, 60);
//
//            if (!rateLimitResult.isAllowed()) {
//                log.warn("Too many requests keyId={}", keyId);
//                throw new RateLimitException("Too many requests", rateLimitResult.retryAfterSeconds());
//            }
//
//            response.setHeader("X-RateLimit-Limit", String.valueOf(requestsPerMinute));
//            response.setHeader("X-RateLimit-Remaining", String.valueOf(rateLimitResult.remaining()));
//
//            var auth = new UsernamePasswordAuthenticationToken(keyId, null,
//                    List.of(new SimpleGrantedAuthority("API_KEY_ROLE"))
//            );
//            SecurityContextHolder.getContext().setAuthentication(auth);
//            merchantContext.setMerchantId(apiKeyEntry.merchantId());
//            merchantContext.setKeyId(apiKeyEntry.keyId());
//            // implementing caching ends---------------------------------------
//            filterChain.doFilter(request, response);
//
//        } catch (Exception e) {
//            handlerExceptionResolver.resolveException(request, response, null, e);
//        }
//
//    }
//
//    private ApiKeyCacheEntry loadAndCache(String keyId){
//        ApiKey apiKey = apiKeyRepository.findByKeyId(keyId).orElse(null);
//        if (apiKey == null) return null;
//        ApiKeyCacheEntry apiKeyCacheEntry = new ApiKeyCacheEntry(
//                apiKey.getKeyId(),
//                apiKey.getKeySecretHash(),
//                apiKey.getPreviousKeySecretHash(),
//                apiKey.getGracePeriodExpiresAt(),
//                apiKey.getMerchant().getId(),
//                apiKey.getEnvironment(),
//                apiKey.isEnabled()
//        );
//        apiKeyCache.put(keyId, apiKeyCacheEntry);
//        return apiKeyCacheEntry;
//    }
//
//
////########################## after caching block this ################
////    private boolean secretMatches(String rawSecret, ApiKey apiKey) {
//////        if (passwordEncoder.matches(rawSecret, apiKey.getKeySecretHash())) {
////        if (BCRYPT.matches(rawSecret, apiKey.getKeySecretHash())) {
////            return true;
////        }
////        //----------manual grace period check is disabled--------------------
//////        boolean isInGracePeriod = apiKey.getGracePeriodExpiresAt() != null &&
//////                LocalDateTime.now().isBefore(apiKey.getGracePeriodExpiresAt());
//////        return isInGracePeriod
//////                && apiKey.getPreviousKeySecretHash() != null
////// //                && passwordEncoder.matches(rawSecret, apiKey.getPreviousKeySecretHash());
//////                && BCRYPT.matches(rawSecret, apiKey.getPreviousKeySecretHash());
////        //-------------manual grace period check is disabled------------------
////         return apiKey.isInGracePeriod()
////                && apiKey.getPreviousKeySecretHash() != null
////                && BCRYPT.matches(rawSecret, apiKey.getPreviousKeySecretHash());
////    }
//
//    //########################## after caching block this ends ################
//    private boolean secretMatches(String rawSecret,
//                                  ApiKeyCacheEntry apiKey) {
//        if (BCRYPT.matches(rawSecret, apiKey.keySecretHash())) {
//            return true;
//        }
//        return apiKey.isInGracePeriod()
//                && apiKey.previousKeySecretHash() != null
//                && BCRYPT.matches(rawSecret, apiKey.previousKeySecretHash());
//    }
//
//    private String[] decode(String header) {
//        String encoded = header.substring(BASIC_PREFIX.length());
//        String decoded = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
//
//        int colon = decoded.indexOf(":");
//        if (colon < 1) return null;
//
//        return new String[]{decoded.substring(0, colon), decoded.substring(colon+1)};
//    }
//}
//
