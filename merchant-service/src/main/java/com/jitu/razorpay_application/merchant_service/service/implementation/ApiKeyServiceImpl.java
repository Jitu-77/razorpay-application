package com.jitu.razorpay_application.merchant_service.service.implementation;

import com.jitu.razorpay_application.common_lib.cache.ApiKeyCache;
import com.jitu.razorpay_application.common_lib.exceptions.ResourceNotFoundException;
import com.jitu.razorpay_application.common_lib.uti.RandomizerUtil;
//import com.jitu.razorpay_application.merchant_service.cache.ApiKeyCache;
import com.jitu.razorpay_application.merchant_service.dto.request.CreateApiKeyRequest;
import com.jitu.razorpay_application.merchant_service.dto.response.ApiKeyCreateResponse;
import com.jitu.razorpay_application.merchant_service.dto.response.ApiKeyResponse;
import com.jitu.razorpay_application.merchant_service.entity.ApiKey;
import com.jitu.razorpay_application.merchant_service.entity.Merchant;
import com.jitu.razorpay_application.merchant_service.mapper.ApiKeyMapper;
import com.jitu.razorpay_application.merchant_service.repository.ApiKeyRepository;
import com.jitu.razorpay_application.merchant_service.repository.MerchantRepository;
import com.jitu.razorpay_application.merchant_service.service.ApiKeyService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true) // this means we are adding on the service level
// and we are importing this from springframework and not Jakarta annotation
//readOnly = true -- avoids dirty checking
public class ApiKeyServiceImpl  implements ApiKeyService {

    private final MerchantRepository merchantRepository;
    private final ApiKeyRepository apiKeyRepository;
    private final ApiKeyMapper apiKeyMapper;
    private BCryptPasswordEncoder BCRPYT = new BCryptPasswordEncoder();
    private final ApiKeyCache apiKeyCache;
    @Override
    @Transactional
    public ApiKeyCreateResponse create(UUID merchantId, CreateApiKeyRequest request) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("merchant", merchantId));
        //String keyId = "rzp_"+request.environment().name().toUpperCase()+"big_random_string";
        String keyId = "rzp_"+request.environment().name().toLowerCase()+"_"+ RandomizerUtil.randomBase64(24);
        // String rawSecret = "big_random_secret"; // TODO: replace with cryptographic random hex
        String rawSecret = RandomizerUtil.randomBase64(40); // TODO: replace with cryptographic random hex
        ApiKey apiKey = ApiKey.builder()
                .merchant(merchant)
                .keyId(keyId)
//                .keySecretHash(rawSecret) // TODO: encode with BcryptPasswordEncoder
                .keySecretHash(BCRPYT.encode(rawSecret))
                .environment(request.environment())
                .build();

        apiKey = apiKeyRepository.save(apiKey);

        return new ApiKeyCreateResponse(apiKey.getId(), keyId, rawSecret, request.environment());
    }

    @Override
    public List<ApiKeyResponse> listByMerchant(UUID merchantId){
        //using mapstruct
//        return apiKeyRepository.findByMerchant_Id(merchantId)
//                .stream()
//                .map(apiKey-> new ApiKeyResponse(
//                        apiKey.getId(),
//                        apiKey.getKeyId(),
//                        apiKey.getEnvironment(),
//                        apiKey.isEnabled(),
//                        apiKey.getLastUsedAt(),
//                        null
//                )).toList();

        return  apiKeyMapper.toResponseList(apiKeyRepository.findByMerchant_Id(merchantId));
    }

    @Override
    @Transactional
    public void revoke(UUID merchantId, UUID keyId) {
        ApiKey apiKey = apiKeyRepository.findById(keyId)
                .filter(apiKey1 -> apiKey1.getMerchant().getId().equals(merchantId))
                //as findById returns a optional so we can apply filter
                .orElseThrow(()-> new ResourceNotFoundException("ApiKey",keyId));
        apiKey.setEnabled(false);
//        apiKeyRepository.save(apiKey); //transactional will take care of this, if we dont write this
        apiKeyCache.evict(apiKey.getKeyId()); // remove from cache layer
    }

    @Override
    @Transactional
    public ApiKeyCreateResponse rotate(UUID merchantId, UUID keyId) {
        ApiKey apiKey = apiKeyRepository.findById(keyId)
                .filter(apiKey1 -> apiKey1.getMerchant().getId().equals(merchantId))
                //as findById returns a optional so we can apply filter
                .orElseThrow(()-> new ResourceNotFoundException("ApiKey",keyId));
        if(!apiKey.isEnabled()) throw new RuntimeException("Cannot rotate a disabled key");
        String newRandomKey = RandomizerUtil.randomBase64(40); //TODO transform
        apiKey.setPreviousKeySecretHash(apiKey.getKeySecretHash());
        apiKey.setKeySecretHash(BCRPYT.encode(newRandomKey));
        apiKey.setRotatedAt(LocalDateTime.now());
        apiKey.setGracePeriodExpiresAt(LocalDateTime.now().plusHours(24));
        apiKey =  apiKeyRepository.save(apiKey);

        apiKeyCache.evict(apiKey.getKeyId()); // remove from cache layer

        return new ApiKeyCreateResponse(apiKey.getId(), apiKey.getKeyId(),
                newRandomKey, apiKey.getEnvironment());
    }
}
