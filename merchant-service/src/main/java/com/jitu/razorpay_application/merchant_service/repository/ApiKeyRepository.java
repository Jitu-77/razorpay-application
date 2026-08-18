package com.jitu.razorpay_application.merchant_service.repository;

import com.jitu.razorpay_application.merchant_service.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {
    List<ApiKey> findByMerchant_Id(UUID merchantId);
    //ApiKey class has merchant class and inside merchant class we have ID
    // so findByMerchant+ _Id >> to get hold of the ID inside merchant class and to go deep down we can append by _

    Optional<ApiKey> findByKeyId(String keyId);
}
