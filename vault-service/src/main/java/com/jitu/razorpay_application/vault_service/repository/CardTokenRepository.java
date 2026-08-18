package com.jitu.razorpay_application.vault_service.repository;

//import com.jitu.RazorPay.vault.entity.CardToken;
import com.jitu.razorpay_application.vault_service.entity.CardToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CardTokenRepository extends JpaRepository<CardToken, UUID> {
    // tokens which are not revoked
    Optional<CardToken> findByTokenAndRevokedAtIsNull(String token);
}
