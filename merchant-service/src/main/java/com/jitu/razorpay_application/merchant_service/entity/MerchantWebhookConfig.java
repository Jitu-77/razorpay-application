package com.jitu.razorpay_application.merchant_service.entity;

import com.jitu.razorpay_application.common_lib.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "merchant_webhook_config",
        indexes = {
                @Index(name = "idx_webhook_merchant_id", columnList = "merchant_id, enabled")
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MerchantWebhookConfig extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id ;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Merchant merchant ;

    @Column(nullable = false, length = 500)
    private String targetUrl ;

    @Column(length = 500)
//    private String webhookSecretHash ;
    private String webhookSecret;// we are going to show this to merchant once
    // they can store it safe , we will use vault service encryption technique only

    @Column(length = 500 ,nullable = false)
    private Boolean enabled ;

    @Column(length = 500)
    private String eventTypes;

    // Comma-separated list of event types to subscribe to

    public boolean isSubscribedTo(String eventType) {
        if (eventTypes == null || eventTypes.isBlank()) {
            return true; // we treat this as all , the user wants to get all the events
        }
        for (String type : eventTypes.split(",")) {
            String trimmed = type.trim();
            if (trimmed.equalsIgnoreCase("ALL") || trimmed.equalsIgnoreCase(eventType)) {
                return true;
            }
        }
        return false;
    }
}
