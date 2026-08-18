package com.jitu.razorpay_application.merchant_service.entity;


import com.jitu.razorpay_application.common_lib.entity.BaseEntity;
import com.jitu.razorpay_application.common_lib.enums.Environment;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "api_key" ,indexes = {
        @Index(name="idx_api_key_merchant_env",columnList = "merchant_id,environment,enabled")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiKey extends BaseEntity {
@Id
@GeneratedValue(strategy =GenerationType.UUID)
private UUID id;

 @ManyToOne(fetch=FetchType.LAZY)
 @JoinColumn(name = "merchant_id")
 private Merchant merchant;

    @Column(nullable = false, length = 50, unique = true)
    private String keyId;
    @Column(nullable = false, length = 200)
    private String keySecretHash;

   @Column(length = 200)
   private String previousKeySecretHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Environment environment;

    @Column(nullable = false)
    @Builder.Default // then it will help to pass default value as we are using @Builder pattern
    private boolean enabled = true;

    private LocalDateTime lastUsedAt;
    private LocalDateTime rotatedAt;
    private LocalDateTime gracePeriodExpiresAt;

    //returning the isInGracePeriod calculation when req
    public boolean isInGracePeriod() {
        return gracePeriodExpiresAt != null && LocalDateTime.now().isBefore(gracePeriodExpiresAt);
    }
}
