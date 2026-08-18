package com.jitu.razorpay_application.vault_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;

import javax.crypto.spec.SecretKeySpec;

@Configuration
public class VaultEncryptionConfig {
    // commented on wk10cl2-------------------------
//    @Value("${vault.master-key}")
//    private String masterKey;
    // commented on wk10cl2-------------------------
    // this is static as we will call this agaian and again so ebery tiome it will generate secure Randiom
    public static BytesEncryptor panEncrypter(byte[] dek) {
        SecretKeySpec decKey = new SecretKeySpec(dek, "AES");
        return new AesBytesEncryptor(decKey, KeyGenerators.secureRandom(12),
                AesBytesEncryptor.CipherAlgorithm.GCM);
    }
    // commented on wk10cl1-------------------------
    //this is a bean to maintain standard value across the app
//    @Bean
//    public BytesEncryptor dekEncrypter() {
//        byte[] masterKeyBytes = Base64.getDecoder().decode(masterKey);
//        SecretKeySpec masterDecKey = new SecretKeySpec(masterKeyBytes, "AES");
//        return new AesBytesEncryptor(masterDecKey, KeyGenerators.secureRandom(12),
//                AesBytesEncryptor.CipherAlgorithm.GCM);
//    }
    // commented on wk10cl1-------------------------
}
