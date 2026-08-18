package com.jitu.razorpay_application.vault_service.repository;

//import com.jitu.RazorPay.vault.entity.VaultCard;
import com.jitu.razorpay_application.vault_service.entity.VaultCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VaultCardRepository extends JpaRepository<VaultCard, UUID> {
}
