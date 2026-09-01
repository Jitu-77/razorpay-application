package com.jitu.razorpay_application.vault_service.services.implementation;

//import com.jitu.RazorPay.common.entity.Money;
//import com.jitu.RazorPay.common.enums.CardBrand;
//import com.jitu.RazorPay.common.exceptions.ResourceNotFoundException;
//import com.jitu.RazorPay.common.uti.RandomizerUtil;
//import com.jitu.RazorPay.payment.processor.PaymentProcessorRouter;
//import com.jitu.RazorPay.payment.processor.dto.PaymentProcessorRequest;
//import com.jitu.RazorPay.payment.processor.dto.PaymentProcessorResponse;
//import com.jitu.RazorPay.vault.config.VaultEncryptionConfig;
//import com.jitu.RazorPay.vault.dto.request.TokenizeRequest;
//import com.jitu.RazorPay.vault.dto.response.TokenizeResponse;
//import com.jitu.RazorPay.vault.entity.CardToken;
//import com.jitu.RazorPay.vault.entity.VaultCard;
//import com.jitu.RazorPay.vault.repository.CardTokenRepository;
//import com.jitu.RazorPay.vault.repository.VaultCardRepository;
//import com.jitu.RazorPay.vault.services.VaultService;


import com.jitu.razorpay_application.common_lib.dto.PaymentProcessorRequest;
import com.jitu.razorpay_application.common_lib.dto.PaymentProcessorResponse;
import com.jitu.razorpay_application.common_lib.entity.Money;
import com.jitu.razorpay_application.common_lib.enums.CardBrand;
import com.jitu.razorpay_application.common_lib.exceptions.ResourceNotFoundException;
import com.jitu.razorpay_application.common_lib.uti.RandomizerUtil;
import com.jitu.razorpay_application.vault_service.config.VaultEncryptionConfig;
import com.jitu.razorpay_application.vault_service.dto.request.TokenizeRequest;
import com.jitu.razorpay_application.vault_service.dto.response.TokenizeResponse;
import com.jitu.razorpay_application.vault_service.entity.CardToken;
import com.jitu.razorpay_application.vault_service.entity.VaultCard;
import com.jitu.razorpay_application.vault_service.processor.CardPaymentProcessor;
import com.jitu.razorpay_application.vault_service.repository.CardTokenRepository;
import com.jitu.razorpay_application.vault_service.repository.VaultCardRepository;
import com.jitu.razorpay_application.vault_service.services.VaultService;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class VaultServiceImpl implements VaultService {

    private final CardTokenRepository cardTokenRepository;
    private final VaultCardRepository vaultCardRepository;
    private final BytesEncryptor dekEncrypter;
//    private final PaymentProcessorRouter paymentProcessorRouter;
    private  final CardPaymentProcessor cardPaymentProcessor;
    @Override
    @Transactional
    public TokenizeResponse tokenize(TokenizeRequest request, UUID merchantId) {
        String lastFour = request.pan().substring(request.pan().length() - 4);
        String bin = request.pan().substring(0, 6);
        CardBrand cardBrand = detectBrand(request.pan());
        byte[] dek = KeyGenerators.secureRandom(32).generateKey();
        byte[] encryptedPan = VaultEncryptionConfig.panEncrypter(dek)
                .encrypt(request.pan().getBytes(StandardCharsets.UTF_8));
        byte[] encryptedDek = dekEncrypter.encrypt(dek);
        VaultCard vaultCard = vaultCardRepository.save(VaultCard.builder()
                .brand(cardBrand)
                .expiryYear(request.expiryYear().toString())
                .expiryMonth(request.expiryMonth().toString())
                .bin(bin)
                .lastFour(lastFour)
                .encryptedDek(encryptedDek)
                .encryptedPan(encryptedPan)
                .cardHolderName(request.cardHolderName())
                .build());

        String token = "tok_" + RandomizerUtil.randomBase64(32);

        cardTokenRepository.save(CardToken.builder()
                .vaultCard(vaultCard)
                .token(token)
                .customer(request.customerId())
                .merchant(merchantId)
                .build());

        return new TokenizeResponse(token, lastFour, cardBrand, request.expiryMonth(), request.expiryYear());
    }

    @Override
    @Transactional
    public PaymentProcessorResponse charge(UUID paymentId, String token, Money amount,
                                           Map<String, Object> methodDetails) {
        CardToken cardToken = cardTokenRepository.findByTokenAndRevokedAtIsNull(token)
                .orElseThrow(() -> new ResourceNotFoundException("CardToken", token));

        VaultCard vaultCard = cardToken.getVaultCard();
        byte[] panBytes = null;

        try {
            byte[] dek = dekEncrypter.decrypt(vaultCard.getEncryptedDek());
            panBytes = VaultEncryptionConfig.panEncrypter(dek).decrypt(vaultCard.getEncryptedPan());

            String pan = new String(panBytes, StandardCharsets.UTF_8);
            String expiry = vaultCard.getExpiryMonth() + "/" + vaultCard.getExpiryYear();

            PaymentProcessorRequest paymentProcessorRequest = PaymentProcessorRequest
                    .card(paymentId, pan, expiry, amount, methodDetails);

//            PaymentProcessorResponse response = paymentProcessorRouter.charge(paymentProcessorRequest);
//            PaymentProcessorResponse response = cardPaymentProcessor.charge(paymentProcessorRequest);
            // for bulk head pattern
            PaymentProcessorResponse response = cardPaymentProcessor.charge(paymentProcessorRequest)
                    .get(5, TimeUnit.SECONDS);

            log.info("Vault charge registered, token={}****", token.substring(0, 4));

            return response;
        } catch (Exception e) {
            log.warn("Vault charge failed, token={}****", token.substring(0, 4));
            return new PaymentProcessorResponse.Failure("VAULT_CHARGE_FAILED", e.getMessage());
        } finally {
            if (panBytes != null) Arrays.fill(panBytes, (byte) 0);
        }
    }


    private CardBrand detectBrand(String pan) {
        if (pan.startsWith("4")) return CardBrand.VISA;
        if (pan.startsWith("5") || pan.startsWith("2")) return CardBrand.MASTERCARD;
        if (pan.startsWith("37") || pan.startsWith("34")) return CardBrand.AMEX;
        return CardBrand.RUPAY;
    }
}
